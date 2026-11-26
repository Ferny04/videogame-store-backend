# Fase 2 · CRUD de catálogos

Base de trabajo: commit `d3a90815206d78158966424d8ff02bb87f202aed` de `Ferny04/videogame-store-backend`. La copia se cotejó archivo por archivo con el commit y el ZIP de la Fase 1. La referencia de implementación fue el ZIP `market-backend-v2-referencia-work(1).zip`, junto con las capturas de Teams, el enunciado y la rúbrica recuperados. La auditoría anterior no estaba disponible como archivo independiente.

## Integrar en Windows

Coloca `videogame-store-backend-fase-2.zip` en Descargas. Si Windows guarda tus descargas en otra carpeta, ajusta la primera ruta del ZIP. Ejecuta este bloque en PowerShell. Crea un respaldo en una carpeta hermana del repositorio y copia únicamente las rutas enumeradas. Cierra la aplicación si la tienes ejecutándose.

```powershell
$ErrorActionPreference = 'Stop'
$repo = 'D:\videogame-store-backend'
$zip = Join-Path $env:USERPROFILE 'Downloads\videogame-store-backend-fase-2.zip'
$stamp = Get-Date -Format 'yyyyMMdd-HHmmss'
$stage = Join-Path $env:TEMP "videogame-store-fase-2-$stamp"
$backup = "D:\videogame-store-backend-respaldo-fase2-$stamp"

if (-not (Test-Path -LiteralPath (Join-Path $repo '.git'))) {
    throw "No se encontró el repositorio en $repo"
}
if (-not (Test-Path -LiteralPath $zip -PathType Leaf)) {
    throw "No se encontró el ZIP en $zip"
}

$baseCommit = 'd3a90815206d78158966424d8ff02bb87f202aed'
git -C $repo merge-base --is-ancestor $baseCommit HEAD
if ($LASTEXITCODE -ne 0) {
    throw 'El historial local debe incluir el commit de la Fase 1.'
}

Expand-Archive -LiteralPath $zip -DestinationPath $stage
$source = Join-Path $stage 'videogame-store-backend'
$paths = @(
    'docs/FASE-2.md',
    'src/main/java/com/merida/tecnm/videogame_store_backend/domain/Client.java',
    'src/main/java/com/merida/tecnm/videogame_store_backend/domain/Console.java',
    'src/main/java/com/merida/tecnm/videogame_store_backend/domain/Videogame.java',
    'src/main/java/com/merida/tecnm/videogame_store_backend/domain/repository/ClientRepository.java',
    'src/main/java/com/merida/tecnm/videogame_store_backend/domain/repository/ConsoleRepository.java',
    'src/main/java/com/merida/tecnm/videogame_store_backend/domain/repository/VideogameRepository.java',
    'src/main/java/com/merida/tecnm/videogame_store_backend/domain/service/CatalogValidation.java',
    'src/main/java/com/merida/tecnm/videogame_store_backend/domain/service/ClientService.java',
    'src/main/java/com/merida/tecnm/videogame_store_backend/domain/service/ConsoleService.java',
    'src/main/java/com/merida/tecnm/videogame_store_backend/domain/service/VideogameService.java',
    'src/main/java/com/merida/tecnm/videogame_store_backend/persistence/ClienteRepository.java',
    'src/main/java/com/merida/tecnm/videogame_store_backend/persistence/ConsolaRepository.java',
    'src/main/java/com/merida/tecnm/videogame_store_backend/persistence/VideojuegoRepository.java',
    'src/main/java/com/merida/tecnm/videogame_store_backend/persistence/crud/ClienteCrudRepository.java',
    'src/main/java/com/merida/tecnm/videogame_store_backend/persistence/crud/ConsolaCrudRepository.java',
    'src/main/java/com/merida/tecnm/videogame_store_backend/persistence/crud/VideojuegoCrudRepository.java',
    'src/main/java/com/merida/tecnm/videogame_store_backend/persistence/mapper/ClientMapper.java',
    'src/main/java/com/merida/tecnm/videogame_store_backend/persistence/mapper/ConsoleMapper.java',
    'src/main/java/com/merida/tecnm/videogame_store_backend/persistence/mapper/VideogameMapper.java',
    'src/main/java/com/merida/tecnm/videogame_store_backend/web/config/CatalogSecurityConfig.java',
    'src/main/java/com/merida/tecnm/videogame_store_backend/web/controller/ApiExceptionHandler.java',
    'src/main/java/com/merida/tecnm/videogame_store_backend/web/controller/ClientController.java',
    'src/main/java/com/merida/tecnm/videogame_store_backend/web/controller/ConsoleController.java',
    'src/main/java/com/merida/tecnm/videogame_store_backend/web/controller/VideogameController.java',
    'src/main/resources/application.properties'
)

foreach ($relative in $paths) {
    if (-not (Test-Path -LiteralPath (Join-Path $source $relative) -PathType Leaf)) {
        throw "Falta en el ZIP: $relative"
    }
}

robocopy $repo $backup /E /XJ /R:1 /W:1
if ($LASTEXITCODE -ge 8) {
    throw "El respaldo falló con código $LASTEXITCODE"
}

foreach ($relative in $paths) {
    $target = Join-Path $repo $relative
    New-Item -ItemType Directory -Path (Split-Path -Parent $target) -Force | Out-Null
    Copy-Item -LiteralPath (Join-Path $source $relative) -Destination $target -Force
}

Set-Location -LiteralPath $repo
git status --short
git diff --check
```

El respaldo incluye la carpeta `.git` y tus archivos privados para que puedas recuperar el proyecto local si lo necesitas. Las dos configuraciones privadas quedan fuera de la lista de copia. Usa la misma base `videogame_store` con el esquema ya instalado.

## Compilar y ejecutar

Con PostgreSQL iniciado, ejecuta desde el repositorio con Java 25 y tu configuración habitual de `dev`:

```powershell
java -version
.\gradlew.bat clean compileJava build
if ($LASTEXITCODE -ne 0) { throw 'Falló la compilación o una prueba.' }

$env:CATALOG_AUTH_USERNAME = 'catalog'
$secret = Read-Host 'Elige una contraseña temporal para las pruebas locales' -AsSecureString
$env:CATALOG_AUTH_PASSWORD = [System.Net.NetworkCredential]::new('', $secret).Password
try {
    .\gradlew.bat --no-daemon bootRun --args='--spring.profiles.active=dev --server.address=127.0.0.1 --server.port=8080'
} finally {
    Remove-Item Env:CATALOG_AUTH_PASSWORD, Env:CATALOG_AUTH_USERNAME -ErrorAction SilentlyContinue
    $secret.Dispose()
}
```

Usa una contraseña temporal distinta a la de PostgreSQL. Si tu perfil privado usa variables para conectarse a la base, mantenlas disponibles en esa terminal como en la Fase 1. Para ejecutar desde IntelliJ, agrega las dos variables de catálogo a tu configuración local de ejecución y usa los mismos argumentos.

En Postman, usa **Basic Auth**, usuario `catalog` y la contraseña temporal. La API atiende en `http://127.0.0.1:8080`. Usa `Content-Type: application/json` en POST y PUT. También puedes probar el acceso desde una segunda ventana de PowerShell. El segundo comando solicita la contraseña.

```powershell
curl.exe -i http://127.0.0.1:8080/consoles
curl.exe -i --user catalog http://127.0.0.1:8080/consoles
```

## Rutas y respuestas

| Método | Consolas | Videojuegos | Clientes | Respuesta esperada con credenciales |
|---|---|---|---|---|
| GET | `/consoles` | `/videogames` | `/clients` | `200` con lista, incluso vacía |
| GET | `/consoles/{id}` | `/videogames/{id}` | `/clients/{id}` | `200`, `404` si falta. `400` con ID incompatible |
| POST | `/consoles` | `/videogames` | `/clients` | `201`, `400` por datos inválidos, `409` por conflicto |
| PUT | `/consoles/{id}` | `/videogames/{id}` | `/clients/{id}` | `200`, `400` por datos inválidos, `404` si falta, `409` por conflicto |
| DELETE | `/consoles/{id}` | `/videogames/{id}` | `/clients/{id}` | `204`, `404` si falta, `409` si el registro está referenciado. `400` con ID incompatible |

En `dev` con Basic Auth configurado, las solicitudes sin credenciales válidas devuelven `401`. Los IDs de consolas y videojuegos son enteros de PostgreSQL y los genera la base en POST. El ID de cliente es un texto de hasta 20 caracteres utilizable como segmento de URL. Usa `CLI-F2-001` o un identificador similar. En PUT manda el registro editable completo. La URL determina el ID y los campos opcionales omitidos de un cliente quedan en `null`.

El precio debe ser no negativo y tener como máximo 14 dígitos enteros y dos decimales significativos. `stock` es un entero no negativo y `active` un booleano obligatorio. En videojuegos, `consoleId` indica la consola y `console` se construye con el catálogo persistido para la respuesta. Un `consoleId` inexistente devuelve `400`.

## Secuencia de pruebas

1. Comprueba que GET sin credenciales devuelve `401`. Con credenciales, prueba las tres rutas base y espera `200`.
2. Envía POST a `/consoles`. Guarda el `consoleId` que devuelve el servidor.

   ```json
   {"name":"Consola Fase 2","active":true}
   ```

3. Envía POST a `/videogames`. Sustituye `1` por el ID real de la consola. Guarda el `videogameId`.

   ```json
   {"name":"Videojuego Fase 2","consoleId":1,"price":799.90,"stock":12,"active":true}
   ```

4. Envía POST a `/clients`. El teléfono se envía como texto para conservar el prefijo y los ceros.

   ```json
   {"clientId":"CLI-F2-001","name":"Ana","lastName":"Pérez","mobile":"+52 999 012 3456","address":"Mérida","email":"ana@example.com"}
   ```

5. Consulta cada registro por su ID. Repite el POST de cliente y el de consola para comprobar `409`. Prueba también un juego con precio negativo, otro con stock negativo, uno sin `active` y uno con un `consoleId` que no exista. Espera `400`.
6. Haz PUT en las tres rutas individuales con los siguientes cuerpos, en el mismo orden de los pasos anteriores. Vuelve a colocar el ID real de la consola en el videojuego.

   ```json
   {"name":"Consola Fase 2 actualizada","active":true}
   ```

   ```json
   {"name":"Videojuego Fase 2 actualizado","consoleId":1,"price":749.90,"stock":8,"active":true}
   ```

   ```json
   {"name":"Ana María","lastName":"Pérez","mobile":"00123","address":"Mérida","email":"ana@example.com"}
   ```

7. Comprueba las filas en pgAdmin. Con el juego asociado, intenta borrar su consola y verifica el `409`.
8. Elimina primero el juego, luego la consola y el cliente de prueba. Cada DELETE devuelve `204`. Repite los DELETE y haz GET y PUT sobre los mismos IDs con cuerpos válidos. Deben devolver `404`. Repite las consultas SQL para confirmar que las filas desaparecieron.

En pgAdmin abre **Query Tool** sobre `videogame_store` y ejecuta:

```sql
SELECT id_consola, nombre, estado
FROM public.consolas
WHERE nombre IN ('Consola Fase 2', 'Consola Fase 2 actualizada');

SELECT v.id_videojuego, v.nombre, v.id_consola, c.nombre AS consola,
       v.precio_venta, v.cantidad_stock, v.estado
FROM public.videojuegos AS v
JOIN public.consolas AS c ON v.id_consola = c.id_consola
WHERE v.nombre IN ('Videojuego Fase 2', 'Videojuego Fase 2 actualizado');

SELECT id, nombre, apellidos, celular, direccion, correo_electronico
FROM public.clientes
WHERE id = 'CLI-F2-001';
```

## Comparación con `market-backend-v2`

| Patrón del curso | Aplicación en la tienda |
|---|---|
| `Category` y `Categoria` | `Console` y `Consola` siguen el mismo flujo de controlador, servicio, interfaz de dominio, repositorio JPA y MapStruct. |
| `Product` y `Producto` | `Videogame` y `Videojuego` conservan el ID escalar y el objeto anidado como en producto/categoría. MapStruct ignora el objeto anidado al escribir y el repositorio carga la consola existente. |
| `Client` y `Cliente` | Se conserva el ID textual y el patrón `EntityManager.persist` para insertar. Si dos peticiones envían el mismo ID, la clave primaria impide que una sobrescriba a la otra. |
| `ResponseEntity`, `Optional`, `@Autowired` y `@RestControllerAdvice` | Se usan las convenciones del maestro. Los tres DELETE devuelven `204` cuando borran un registro, como categorías y clientes en la referencia. |
| Actualización de producto con `@MappingTarget` | Se usa en los tres catálogos. La transacción bloquea la fila durante PUT y DELETE para aplicar el cambio sobre una fila existente y resolver escrituras simultáneas de forma consistente. |

`BigDecimal` conserva la precisión de `numeric(16,2)`. El teléfono es `String` porque el esquema permite prefijos y ceros al inicio. `Boolean` e `Integer` permiten distinguir un campo ausente de `false` o `0`. Se añadió una opción de Jackson para rechazar fracciones al leer enteros y evitar que un stock como `-0.5` se convierta en cero.

`CatalogSecurityConfig` habilita Basic Auth sin sesión para pruebas con un cliente HTTP local cuando el perfil `dev` tiene una contraseña de catálogo. Usa un usuario en memoria y una contraseña que se recibe del entorno. La cadena de seguridad exige autenticación y omite CSRF en esa configuración temporal. Arranca el perfil local en `127.0.0.1` con los argumentos indicados. La configuración de JWT y OpenAPI pertenece a la Fase 4.

## Verificación ejecutada

| Verificación | Resultado observado |
|---|---|
| Gradle Wrapper 9.9.2 con Temurin 25.0.5.1 y Spring Boot 4.1.3 | `./gradlew --offline --no-daemon clean compileJava build` terminó con `BUILD SUCCESSFUL`. El primer intento de descarga del wrapper falló por red y se reintentó con la distribución local de la misma versión. |
| MapStruct 1.6.3 | Generó `ConsoleMapperImpl`, `VideogameMapperImpl` y `ClientMapperImpl`. |
| Spring y PostgreSQL | La prueba original `contextLoads` pasó. La aplicación arrancó con `dev` y `pdn`, con HikariCP y `ddl-auto=validate`. El primer intento del contexto perdió acceso a la instancia entre comandos, y el build pasó tras mantener PostgreSQL activo durante toda la prueba. |
| PostgreSQL de verificación | Versión 18.7, base desechable `videogame_store`, puerto local 55484, esquema `public` y seis tablas creadas con el SQL original. La instancia de pruebas escuchó solo en loopback. |
| HTTP y persistencia | 99 peticiones comprobadas en `dev` y 6 comprobaciones adicionales de acceso en `pdn`. Se verificaron las 15 rutas, persistencia mediante SQL, cambios de consola, precio exacto, borrados, restricciones FK, ausencia de registros, solicitudes inválidas y escrituras concurrentes de clientes. |

En la verificación se insertaron referencias de venta directamente en la base aislada y se comprobó que impedían borrar un cliente o un videojuego. Las pruebas de Windows, IntelliJ y PostgreSQL 18.6 corresponden a tu integración local.

## Inventario exacto

### Nuevos

- `docs/FASE-2.md`
- `src/main/java/com/merida/tecnm/videogame_store_backend/domain/Client.java`
- `src/main/java/com/merida/tecnm/videogame_store_backend/domain/Console.java`
- `src/main/java/com/merida/tecnm/videogame_store_backend/domain/Videogame.java`
- `src/main/java/com/merida/tecnm/videogame_store_backend/domain/repository/ClientRepository.java`
- `src/main/java/com/merida/tecnm/videogame_store_backend/domain/repository/ConsoleRepository.java`
- `src/main/java/com/merida/tecnm/videogame_store_backend/domain/repository/VideogameRepository.java`
- `src/main/java/com/merida/tecnm/videogame_store_backend/domain/service/CatalogValidation.java`
- `src/main/java/com/merida/tecnm/videogame_store_backend/domain/service/ClientService.java`
- `src/main/java/com/merida/tecnm/videogame_store_backend/domain/service/ConsoleService.java`
- `src/main/java/com/merida/tecnm/videogame_store_backend/domain/service/VideogameService.java`
- `src/main/java/com/merida/tecnm/videogame_store_backend/persistence/ClienteRepository.java`
- `src/main/java/com/merida/tecnm/videogame_store_backend/persistence/ConsolaRepository.java`
- `src/main/java/com/merida/tecnm/videogame_store_backend/persistence/VideojuegoRepository.java`
- `src/main/java/com/merida/tecnm/videogame_store_backend/persistence/crud/ClienteCrudRepository.java`
- `src/main/java/com/merida/tecnm/videogame_store_backend/persistence/crud/ConsolaCrudRepository.java`
- `src/main/java/com/merida/tecnm/videogame_store_backend/persistence/crud/VideojuegoCrudRepository.java`
- `src/main/java/com/merida/tecnm/videogame_store_backend/persistence/mapper/ClientMapper.java`
- `src/main/java/com/merida/tecnm/videogame_store_backend/persistence/mapper/ConsoleMapper.java`
- `src/main/java/com/merida/tecnm/videogame_store_backend/persistence/mapper/VideogameMapper.java`
- `src/main/java/com/merida/tecnm/videogame_store_backend/web/config/CatalogSecurityConfig.java`
- `src/main/java/com/merida/tecnm/videogame_store_backend/web/controller/ApiExceptionHandler.java`
- `src/main/java/com/merida/tecnm/videogame_store_backend/web/controller/ClientController.java`
- `src/main/java/com/merida/tecnm/videogame_store_backend/web/controller/ConsoleController.java`
- `src/main/java/com/merida/tecnm/videogame_store_backend/web/controller/VideogameController.java`

### Modificados

- `src/main/resources/application.properties`

### Conservados de la Fase 1

- `.gitattributes`
- `.gitignore`
- `build.gradle`
- `docs/application-dev.properties.example`
- `docs/application-pdn.properties.example`
- `docs/videogame-store.sql`
- `gradle/wrapper/gradle-wrapper.jar`
- `gradle/wrapper/gradle-wrapper.properties`
- `gradlew`
- `gradlew.bat`
- `settings.gradle`
- `src/main/java/com/merida/tecnm/videogame_store_backend/VideogameStoreBackendApplication.java`
- `src/main/java/com/merida/tecnm/videogame_store_backend/persistence/entity/Cliente.java`
- `src/main/java/com/merida/tecnm/videogame_store_backend/persistence/entity/Consola.java`
- `src/main/java/com/merida/tecnm/videogame_store_backend/persistence/entity/Usuario.java`
- `src/main/java/com/merida/tecnm/videogame_store_backend/persistence/entity/Venta.java`
- `src/main/java/com/merida/tecnm/videogame_store_backend/persistence/entity/VentaVideojuego.java`
- `src/main/java/com/merida/tecnm/videogame_store_backend/persistence/entity/VentaVideojuegoPK.java`
- `src/main/java/com/merida/tecnm/videogame_store_backend/persistence/entity/Videojuego.java`
- `src/test/java/com/merida/tecnm/videogame_store_backend/VideogameStoreBackendApplicationTests.java`

**Eliminados:** ninguno del commit base.

Después de comprobar los cambios, puedes usar la variable `$paths` del bloque de integración, en la misma terminal, para preparar únicamente los archivos de esta fase:

```powershell
Set-Location -LiteralPath 'D:\videogame-store-backend'
git add -- @paths
git diff --cached --stat
git commit -m 'feat: implement catalog CRUD operations'
```
