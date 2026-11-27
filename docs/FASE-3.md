# Fase 3 · Ventas Maestro-Detalle

La API registra una venta y todos sus videojuegos con una sola petición. El cliente y los videojuegos deben existir en los catálogos. Se conserva el esquema de seis tablas y `ddl-auto=validate`.

La base es el commit `3091b97ca864d8ee89517a78039e64979a28f1da`. Se cotejaron las huellas de sus 46 archivos con el árbol publicado en GitHub y el ZIP de la Fase 2. Para la implementación se revisaron el enunciado y la rúbrica de Teams, el SQL y ZIP de la Fase 1, la guía `docs/FASE-2.md` y la copia de `market-backend-v2` con las ADA 11 y 12.

## Cómo funciona la persistencia

1. `SaleController` recibe un `SaleRequest`. `SaleService` valida los IDs y las cantidades, consulta los catálogos y calcula los importes en una transacción.
2. MapStruct convierte la solicitud en modelos de dominio y convierte esos modelos en entidades. El detalle lleva el ID del videojuego en `VentaVideojuegoPK` y el repositorio asigna la venta a cada detalle.
3. `VentaRepository.save` llama una sola vez a `ventaCrudRepository.save(venta)`. Hibernate inserta el maestro y los detalles mediante `CascadeType.ALL`. `@MapsId("idVenta")` toma el ID generado de la venta y lo coloca en la clave del detalle.
4. `@EmbeddedId` representa la clave primaria `(id_venta, id_videojuego)`. Un videojuego puede aparecer en varias ventas y solo una vez en cada venta. `mappedBy = "venta"` indica que el lado que mantiene la relación es el campo `venta` del detalle.
5. En PUT, el repositorio bloquea la venta durante la transacción y compara los detalles por ID de videojuego. Actualiza los que permanecen, enlaza los nuevos y retira los omitidos. `orphanRemoval = true` elimina las filas retiradas de la colección. Se conserva la fecha original y el estado de la venta.
6. En DELETE, Hibernate elimina primero los detalles y luego la venta. Las claves foráneas del SQL usan el comportamiento predeterminado `NO ACTION`. Los catálogos carecen de cascada desde la venta o sus detalles.

Si el servicio rechaza una solicitud no se escribe la venta. Si falla una inserción o actualización en PostgreSQL, Spring revierte toda la transacción. Las respuestas usan `Sale` y `SaleItem` y la conversión excluye las referencias inversas a la venta para evitar recursión.

## Comparación con el código del maestro

| Referencia del curso | Implementación de ventas |
|---|---|
| `PurchaseController`, `PurchaseService`, `PurchaseRepository`, `CompraRepository` | Se sigue la misma arquitectura, con `ResponseEntity`, `Optional`, repositorios y transacciones de servicio. |
| `PurchaseRequest`, `PurchaseItemRequest` | `SaleRequest` y `SaleItemRequest` separan los campos editables del resultado. Los nombres JSON se mantienen en inglés como en los catálogos de la tienda. |
| `CompraProductoPK`, `@MapsId`, `cascade`, `orphanRemoval` | Se usan las relaciones aprobadas de `Venta` y `VentaVideojuego` y la actualización de la colección por ID de videojuego. PUT y DELETE adquieren un bloqueo sobre la fila de la venta, como las escrituras de catálogos de la Fase 2. |
| `PurchaseMapper` y `PurchaseItemMapper` | Se mantienen la conversión de colecciones y el mapeo de la clave anidada con MapStruct. `ListCrudRepository` conserva las operaciones CRUD y devuelve la lista sin conversión de tipo. |
| La ADA 11 recibe el importe de cada producto | La tienda toma `precio_venta` de PostgreSQL, multiplica por la cantidad con `BigDecimal` y valida el límite de `numeric(16,2)`. |
| La ADA 11 asigna la fecha, estado `A` y detalles activos | Se mantienen esas convenciones. El servidor acepta `E` o `EFECTIVO`, `T` o `TARJETA` y códigos de una letra de `A` a `Z`, como en la referencia. |

La rúbrica pide demostrar persistencia Maestro-Detalle. La consigna y el flujo de compras revisados no establecen una disminución automática de stock. Registrar, editar o borrar ventas conserva las existencias y los datos de los catálogos.

## Contrato HTTP

| Método | Ruta | Respuesta exitosa | Errores aplicables |
|---|---|---|---|
| GET | `/sales` | `200` con la lista, incluso vacía | `401` sin credenciales válidas en `dev` |
| GET | `/sales/{id}` | `200` | `400` si el ID es inválido, `404` si la venta no existe, `401` |
| POST | `/sales` | `201` con venta y detalles | `400` datos inválidos, `404` catálogo referenciado inexistente, `409` conflicto de integridad, `401` |
| PUT | `/sales/{id}` | `200` con la venta actualizada | `400`, `404` venta o catálogo referenciado inexistente, `409`, `401` |
| DELETE | `/sales/{id}` | `204` sin cuerpo | `400` ID inválido, `404` venta inexistente, `409` ante un conflicto de integridad, `401` |

El perfil `pdn` usa la seguridad de Spring sin el usuario temporal y bloquea peticiones de negocio. Una solicitud rechazada por seguridad puede devolver `401` o `403` según la cadena y el tipo de petición. Las 15 rutas de consolas, videojuegos y clientes se conservan con el contrato de `docs/FASE-2.md`.

`clientId` es obligatorio, admite hasta 20 caracteres y se usa tal como está registrado. `paymentMethod` es obligatorio. `comment` es opcional y admite hasta 300 caracteres. `items` debe contener al menos un renglón. Sus IDs y cantidades son enteros positivos de 32 bits y los IDs no se pueden repetir. PUT recibe la cabecera editable y la lista completa. Si se omite el comentario, queda en `null`. En PUT los importes se recalculan con el precio de catálogo vigente al consultar cada juego. GET muestra los importes almacenados en los detalles. El orden de las listas no forma parte del contrato.

### POST `/sales`

Usa IDs que existan. El ejemplo supone que 1 cuesta 799.90 y 2 cuesta 999.00.

```json
{
  "clientId": "CLI-F3-001",
  "paymentMethod": "E",
  "comment": "Venta de prueba Fase 3",
  "items": [
    {"videogameId": 1, "quantity": 2},
    {"videogameId": 2, "quantity": 1}
  ]
}
```

Ejemplo de respuesta `201`. `saleId` y `date` los asigna el servidor. La fecha es local al servidor y PostgreSQL la guarda sin zona horaria. El `total` exterior suma los importes de los detalles al responder, sin una columna adicional en la tabla `ventas`.

```json
{
  "saleId": 1,
  "clientId": "CLI-F3-001",
  "date": "2026-11-27T11:15:30",
  "paymentMethod": "E",
  "comment": "Venta de prueba Fase 3",
  "state": "A",
  "items": [
    {"videogameId": 1, "quantity": 2, "total": 1599.80, "active": true},
    {"videogameId": 2, "quantity": 1, "total": 999.00, "active": true}
  ],
  "total": 2598.80
}
```

### PUT `/sales/1`

Este cuerpo actualiza la cantidad de 1, retira 2 e incorpora 3. Si el juego 3 cuesta 129.25, el nuevo total es 2528.95.

```json
{
  "clientId": "CLI-F3-001",
  "paymentMethod": "TARJETA",
  "comment": "Actualización de prueba Fase 3",
  "items": [
    {"videogameId": 1, "quantity": 3},
    {"videogameId": 3, "quantity": 1}
  ]
}
```

## Integración en Windows

### 1. Revisar, respaldar y copiar

Guarda `videogame-store-backend-fase-3.zip` en Descargas o ajusta `$zip` a la ruta real. Detén el backend de la Fase 2 y ejecuta en PowerShell:

```powershell
$ErrorActionPreference = 'Stop'
$repo = 'D:\videogame-store-backend'
$backup = 'D:\videogame-store-backend-RESPALDO-ANTES-FASE3'
$zip = Join-Path $env:USERPROFILE 'Downloads\videogame-store-backend-fase-3.zip'
$stage = Join-Path $env:TEMP ('videogame-store-fase3-' + [guid]::NewGuid().ToString('N'))
$baseCommit = '3091b97ca864d8ee89517a78039e64979a28f1da'

Set-Location -LiteralPath $repo
$head = git rev-parse HEAD
if ($LASTEXITCODE -ne 0 -or $head -ne $baseCommit) { throw 'Verifica que tu HEAD sea el commit 3091b97 de la Fase 2.' }
$pending = git status --porcelain
if ($LASTEXITCODE -ne 0 -or $pending) { throw 'Revisa tus cambios locales antes de integrar.' }
if (-not (Test-Path -LiteralPath $zip -PathType Leaf)) { throw "No se encontró el ZIP en $zip." }
if (Test-Path -LiteralPath $backup) { throw "El respaldo ya existe en $backup. Conserva esa copia antes de volver a ejecutar." }

robocopy $repo $backup /E /XJ /R:1 /W:1
if ($LASTEXITCODE -ge 8) { throw "Falló el respaldo. Robocopy devolvió $LASTEXITCODE." }

Expand-Archive -LiteralPath $zip -DestinationPath $stage
$source = Join-Path $stage 'videogame-store-backend'
$paths = @(
    'docs/FASE-3.md',
    'src/main/java/com/merida/tecnm/videogame_store_backend/domain/Sale.java',
    'src/main/java/com/merida/tecnm/videogame_store_backend/domain/SaleItem.java',
    'src/main/java/com/merida/tecnm/videogame_store_backend/domain/SaleItemRequest.java',
    'src/main/java/com/merida/tecnm/videogame_store_backend/domain/SaleRequest.java',
    'src/main/java/com/merida/tecnm/videogame_store_backend/domain/repository/SaleRepository.java',
    'src/main/java/com/merida/tecnm/videogame_store_backend/domain/service/SaleService.java',
    'src/main/java/com/merida/tecnm/videogame_store_backend/persistence/VentaRepository.java',
    'src/main/java/com/merida/tecnm/videogame_store_backend/persistence/crud/VentaCrudRepository.java',
    'src/main/java/com/merida/tecnm/videogame_store_backend/persistence/mapper/SaleItemMapper.java',
    'src/main/java/com/merida/tecnm/videogame_store_backend/persistence/mapper/SaleMapper.java',
    'src/main/java/com/merida/tecnm/videogame_store_backend/web/config/CatalogSecurityConfig.java',
    'src/main/java/com/merida/tecnm/videogame_store_backend/web/controller/SaleController.java'
)
foreach ($relative in $paths) {
    if (-not (Test-Path -LiteralPath (Join-Path $source $relative) -PathType Leaf)) {
        throw "Falta en el ZIP: $relative"
    }
}

foreach ($relative in $paths) {
    $target = Join-Path $repo $relative
    New-Item -ItemType Directory -Path (Split-Path -Parent $target) -Force | Out-Null
    Copy-Item -LiteralPath (Join-Path $source $relative) -Destination $target -Force
}

git status --short --untracked-files=all
git diff --check
```

El respaldo incluye `.git` y tus configuraciones locales. La lista de copia solo comprende las rutas de la fase. El ZIP usa el mismo SQL y la misma configuración de base de datos que ya tienes, de modo que puedes usar `videogame_store` tal como está.

### 2. Compilar y arrancar

En la misma terminal, con PostgreSQL iniciado y tu perfil privado `dev` configurado, ejecuta:

```powershell
java -version
.\gradlew.bat compileJava
if ($LASTEXITCODE -ne 0) { throw 'Falló compileJava.' }
.\gradlew.bat build
if ($LASTEXITCODE -ne 0) { throw 'Falló el build.' }

$env:CATALOG_AUTH_USERNAME = 'catalog'
$catalogSecret = Read-Host 'Elige una contraseña temporal para la API local' -AsSecureString
$env:CATALOG_AUTH_PASSWORD = [System.Net.NetworkCredential]::new('', $catalogSecret).Password
try {
    .\gradlew.bat --no-daemon bootRun --args='--spring.profiles.active=dev --server.address=127.0.0.1 --server.port=8080'
} finally {
    Remove-Item Env:CATALOG_AUTH_USERNAME, Env:CATALOG_AUTH_PASSWORD -ErrorAction SilentlyContinue
    $catalogSecret.Dispose()
}
```

Si tu perfil utiliza variables para PostgreSQL, restaura las de tu configuración local de la Fase 2 en esa terminal. Para IntelliJ, usa los mismos argumentos y agrega las variables de Basic Auth a la configuración local de ejecución. La contraseña temporal entra por el entorno. `CatalogSecurityConfig` exige autenticación para todas las rutas y la configuración temporal solo se activa en `dev` sin `pdn`.

### 3. Probar desde una segunda ventana de PowerShell

Pega este bloque y escribe la misma contraseña temporal. La función valida el código HTTP antes de devolver datos y decodifica la respuesta en UTF-8. El script usa IDs recién generados para no confundir los resultados con filas de pruebas anteriores.

```powershell
$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.Net.Http
$api = 'http://127.0.0.1:8080'
$catalogSecret = Read-Host 'Contraseña temporal del backend' -AsSecureString
$localPassword = [System.Net.NetworkCredential]::new('', $catalogSecret).Password
$basic = [Convert]::ToBase64String([Text.Encoding]::UTF8.GetBytes("catalog:$localPassword"))
Remove-Variable localPassword
$catalogSecret.Dispose()
$http = [System.Net.Http.HttpClient]::new()

function Invoke-Phase3Api {
    param(
        [string]$Method,
        [string]$Path,
        [int]$Expected,
        [object]$Body,
        [switch]$Anonymous
    )
    $request = [System.Net.Http.HttpRequestMessage]::new([System.Net.Http.HttpMethod]::new($Method), "$api$Path")
    $response = $null
    try {
        if (-not $Anonymous) {
            $request.Headers.Authorization = [System.Net.Http.Headers.AuthenticationHeaderValue]::new('Basic', $basic)
        }
        if ($null -ne $Body) {
            $json = ConvertTo-Json -InputObject $Body -Depth 8 -Compress
            $request.Content = [System.Net.Http.StringContent]::new($json, [Text.Encoding]::UTF8, 'application/json')
        }
        $response = $http.SendAsync($request).GetAwaiter().GetResult()
        $bytes = $response.Content.ReadAsByteArrayAsync().GetAwaiter().GetResult()
        $text = [Text.Encoding]::UTF8.GetString($bytes)
        $status = [int]$response.StatusCode
        if ($status -ne $Expected) {
            throw "$Method $Path devolvió $status. Esperaba $Expected. $text"
        }
        Write-Host "$Method $Path -> $status"
        if ($status -in 200, 201 -and $text) { ConvertFrom-Json -InputObject $text }
    } finally {
        if ($null -ne $response) { $response.Dispose() }
        $request.Dispose()
    }
}

$sale = $null
$console = $null
$client = $null
$tag = [guid]::NewGuid().ToString('N').Substring(0, 8)
$clientId = "CLI-F3-$tag"

Invoke-Phase3Api GET '/sales' 401 -Anonymous
$console = Invoke-Phase3Api POST '/consoles' 201 @{name = "Consola F3 $tag"; active = $true}
$client = Invoke-Phase3Api POST '/clients' 201 @{clientId = $clientId; name = 'Andrés'}
$games = @(
    Invoke-Phase3Api POST '/videogames' 201 @{name = "Acción $tag"; consoleId = $console.consoleId; price = 799.90; stock = 10; active = $true}
    Invoke-Phase3Api POST '/videogames' 201 @{name = "Aventura $tag"; consoleId = $console.consoleId; price = 999.00; stock = 10; active = $true}
    Invoke-Phase3Api POST '/videogames' 201 @{name = "Estrategia $tag"; consoleId = $console.consoleId; price = 129.25; stock = 10; active = $true}
)
$create = @{
    clientId = $client.clientId
    paymentMethod = 'E'
    comment = "Creación F3 $tag"
    items = @(
        @{videogameId = $games[0].videogameId; quantity = 2}
        @{videogameId = $games[1].videogameId; quantity = 1}
    )
}
$sale = Invoke-Phase3Api POST '/sales' 201 $create
if ($sale.items.Count -ne 2 -or [decimal]$sale.total -ne 2598.80) {
    throw 'La venta inicial debe tener dos detalles y un total de 2598.80.'
}
$salePath = "/sales/$($sale.saleId)"
$sale | ConvertTo-Json -Depth 8
Invoke-Phase3Api GET $salePath 200
$listed = Invoke-Phase3Api GET '/sales' 200
$matches = @($listed | Where-Object { $_.saleId -eq $sale.saleId })
if ($matches.Count -ne 1) { throw 'La venta recién creada debe aparecer en la lista.' }
Invoke-Phase3Api DELETE "/clients/$clientId" 409
Invoke-Phase3Api DELETE "/videogames/$($games[0].videogameId)" 409

$invalid = $create.Clone()
$invalid.comment = "Rechazado F3 $tag"
$invalid.items = @(
    $create.items[0]
    @{videogameId = $games[1].videogameId; quantity = 0}
)
Invoke-Phase3Api POST '/sales' 400 $invalid
Write-Host "ID de venta para pgAdmin: $($sale.saleId). Etiqueta: $tag."
```

En este punto ejecuta las consultas de pgAdmin. Debes ver dos detalles, importes 1599.80 y 999.00 y una venta de 2598.80. La consulta del intento rechazado debe devolver cero filas. Luego ejecuta en la segunda ventana:

```powershell
$update = @{
    clientId = $clientId
    paymentMethod = 'TARJETA'
    comment = "Actualización F3 $tag"
    items = @(
        @{videogameId = $games[0].videogameId; quantity = 3}
        @{videogameId = $games[2].videogameId; quantity = 1}
    )
}
$updatedSale = $null
$updatedSale = Invoke-Phase3Api PUT $salePath 200 $update
if ($updatedSale.saleId -ne $sale.saleId -or $updatedSale.date -ne $sale.date -or [decimal]$updatedSale.total -ne 2528.95) {
    throw 'El PUT debe conservar el ID y la fecha y actualizar el total a 2528.95.'
}
$updatedSale | ConvertTo-Json -Depth 8
```

Repite las consultas. Espera el mismo ID y fecha, la baja del segundo detalle, el alta del tercero y un total de 2528.95. Después ejecuta:

```powershell
Invoke-Phase3Api DELETE $salePath 204
Invoke-Phase3Api GET $salePath 404
Invoke-Phase3Api PUT $salePath 404 $update
Invoke-Phase3Api DELETE $salePath 404
Invoke-Phase3Api GET "/clients/$clientId" 200
foreach ($game in $games) {
    Invoke-Phase3Api GET "/videogames/$($game.videogameId)" 200
}
```

Repite las consultas en pgAdmin. Las de maestro y detalle deben quedar vacías y los videojuegos deben seguir con stock 10. Cuando hayas comprobado eso, puedes retirar los datos de prueba creados arriba:

```powershell
foreach ($game in $games) {
    Invoke-Phase3Api DELETE "/videogames/$($game.videogameId)" 204
}
Invoke-Phase3Api DELETE "/clients/$clientId" 204
Invoke-Phase3Api DELETE "/consoles/$($console.consoleId)" 204
$http.Dispose()
Remove-Variable basic
```

## Verificar en pgAdmin

Abre **Query Tool** en `videogame_store`. Sustituye `1` por el `saleId` obtenido en PowerShell y `ETIQUETA` por el valor de `$tag`. Usa el mismo ID antes y después de PUT y DELETE.

```sql
SELECT id_venta, id_cliente, fecha, medio_pago, comentario, estado
FROM public.ventas
WHERE id_venta = 1;

SELECT id_venta, id_videojuego, cantidad, total, estado
FROM public.ventas_videojuegos
WHERE id_venta = 1
ORDER BY id_videojuego;

SELECT count(*) AS detalles, COALESCE(sum(total), 0) AS total_venta
FROM public.ventas_videojuegos
WHERE id_venta = 1;

SELECT id_venta
FROM public.ventas
WHERE comentario = 'Rechazado F3 ETIQUETA';

SELECT id, nombre FROM public.clientes WHERE id = 'CLI-F3-ETIQUETA';
SELECT id_videojuego, nombre, cantidad_stock
FROM public.videojuegos
WHERE nombre IN ('Acción ETIQUETA', 'Aventura ETIQUETA', 'Estrategia ETIQUETA');

SELECT contype, pg_get_constraintdef(oid) AS definicion
FROM pg_constraint
WHERE conrelid = 'public.ventas_videojuegos'::regclass
  AND contype IN ('p', 'f')
ORDER BY contype;
```

La consulta de restricciones muestra la clave primaria compuesta y las FK sin cláusula `ON DELETE CASCADE`. En las pruebas automatizadas, intentar el DELETE de una venta directamente con SQL mientras tenía detalles produjo `23503`. El DELETE de la API sí borró maestro y detalles, y el log de Hibernate mostró las operaciones DELETE en ambas tablas. Al crear la venta se observaron los INSERT de la cabecera y de los dos detalles, aunque el código solo llama a `save` sobre el maestro. Los datos de catálogo siguieron presentes después del borrado.

## Resultados ejecutados

| Comprobación | Resultado observado |
|---|---|
| Compilación, build y contexto | `./gradlew --offline --no-daemon clean compileJava build` terminó con `BUILD SUCCESSFUL` usando Gradle Wrapper 9.9.2, Temurin 25.0.5.1 y Spring Boot 4.1.3. La prueba original `contextLoads` pasó. MapStruct 1.6.3 generó los cinco mapeadores, incluidos `SaleMapperImpl` y `SaleItemMapperImpl`. |
| PostgreSQL y Hibernate | PostgreSQL 18.7 aislado, base `videogame_store`, puerto 55485 en loopback. Se instaló el SQL original y la aplicación arrancó con `ddl-auto=validate`. |
| Regresión de catálogos | 99 peticiones verificaron las 15 rutas, validación, CRUD, conflictos e integridad referencial. |
| Ventas | 66 peticiones en el escenario de ventas, incluidos datos de catálogo de apoyo. Se ejercitaron las cinco rutas, altas y bajas de detalles en PUT, un cambio de cliente, dos PUT simultáneos, montos fuera de rango, cantidades inválidas, duplicados y referencias inexistentes. |
| Cascade y clave compuesta | SQL verificó una cabecera y dos detalles para el POST inicial, importes exactos, altas y bajas de detalles en PUT y eliminación completa por DELETE. PostgreSQL rechazó una clave compuesta duplicada con `23505` y el DELETE directo de la cabecera con `23503`. Los catálogos y el stock siguieron intactos. |
| Atomicidad frente a fallos de base | Una restricción CHECK temporal en la base desechable forzó el fallo de un detalle en POST y en PUT. Ambos respondieron `409`. Tras POST quedaron cero ventas y cero detalles. Tras PUT se conservaron la cabecera y los detalles previos. La restricción temporal se retiró al terminar cada prueba. |
| Seguridad temporal | `dev` rechazó accesos anónimos a ventas. En `pdn` y `dev,pdn` pasaron 16 comprobaciones de rechazo sobre las cuatro rutas base, con solicitudes anónimas y con credenciales válidas de `dev`. |

La prueba inicial del cliente HTTP usó la codificación incorrecta para un cuerpo con acentos. Se corrigió el cliente de pruebas para enviar UTF-8 y se repitió la verificación completa. Los bloques de PowerShell de esta guía quedan por ejecutar en tu Windows con Temurin 25.0.4.1 y PostgreSQL 18.6.

## Inventario exacto del tercer commit

### Nuevos

- `docs/FASE-3.md`
- `src/main/java/com/merida/tecnm/videogame_store_backend/domain/Sale.java`
- `src/main/java/com/merida/tecnm/videogame_store_backend/domain/SaleItem.java`
- `src/main/java/com/merida/tecnm/videogame_store_backend/domain/SaleItemRequest.java`
- `src/main/java/com/merida/tecnm/videogame_store_backend/domain/SaleRequest.java`
- `src/main/java/com/merida/tecnm/videogame_store_backend/domain/repository/SaleRepository.java`
- `src/main/java/com/merida/tecnm/videogame_store_backend/domain/service/SaleService.java`
- `src/main/java/com/merida/tecnm/videogame_store_backend/persistence/VentaRepository.java`
- `src/main/java/com/merida/tecnm/videogame_store_backend/persistence/crud/VentaCrudRepository.java`
- `src/main/java/com/merida/tecnm/videogame_store_backend/persistence/mapper/SaleItemMapper.java`
- `src/main/java/com/merida/tecnm/videogame_store_backend/persistence/mapper/SaleMapper.java`
- `src/main/java/com/merida/tecnm/videogame_store_backend/web/controller/SaleController.java`

### Modificados

- `src/main/java/com/merida/tecnm/videogame_store_backend/web/config/CatalogSecurityConfig.java`

El cambio a `@Profile("dev & !pdn")` impide iniciar Basic Auth temporal cuando también se activa `pdn`. La regla `anyRequest().authenticated()` ya cubre `/sales`.

**Eliminados:** ninguno.

Se conservan byte por byte los otros 45 archivos del commit base. Entre ellos están el SQL, las siete entidades JPA, los controladores y servicios de catálogos, `ApiExceptionHandler.java`, la prueba de contexto, los perfiles de ejemplo, `.gitignore`, el wrapper, las dependencias y `docs/FASE-2.md`.

## Preparar el commit

De vuelta en la primera ventana de PowerShell, detén el backend con Ctrl+C. Conserva la variable `$paths` del bloque de integración. Comprueba que los perfiles privados siguen ignorados y que el área de preparación contiene solo las rutas previstas.

```powershell
Set-Location -LiteralPath 'D:\videogame-store-backend'
git status --short --untracked-files=all
$privatePaths = @(
    'src/main/resources/application-dev.properties',
    'src/main/resources/application-pdn.properties',
    '.env'
)
foreach ($relative in $privatePaths) {
    git check-ignore -q -- $relative
    if ($LASTEXITCODE -ne 0) { throw "Git no está ignorando $relative" }
}
$alreadyStaged = git diff --cached --name-only
if ($alreadyStaged) { throw 'Revisa los archivos que ya están preparados antes de continuar.' }

git add -- @paths
if ($LASTEXITCODE -ne 0) { throw 'Falló git add.' }
$staged = @(git -c core.quotepath=false diff --cached --name-only)
$difference = Compare-Object ($paths | Sort-Object) ($staged | Sort-Object)
if ($difference) { throw 'El inventario preparado difiere del inventario de la fase.' }
git diff --cached --check
if ($LASTEXITCODE -ne 0) { throw 'Revisa los errores del diff.' }
git diff --cached --stat
git --no-pager diff --cached
```

Revisa el diff preparado y confirma que no haya credenciales reales. Cuando termines de comprobar la fase en tu equipo, el comando del tercer commit es:

```powershell
git commit -m 'feat: implement sales master-detail persistence'
```
