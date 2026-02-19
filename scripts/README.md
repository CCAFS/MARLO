# Scripts for MARLO

All scripts live in this `scripts/` folder. Run them **from the repo root**:

| Platform  | Java 17              | Java 8               |
|-----------|----------------------|----------------------|
| Mac/Linux | `./scripts/run-marlo-java17.sh` | `./scripts/run-marlo-java8.sh` |
| Windows   | `scripts\run-marlo-java17.bat`  | `scripts\run-marlo-java8.bat`  |

Each run script changes to the repo root, runs Maven, calls the corresponding update script (in this folder), then starts the server.

## URL convention in `marlo-dev.properties`

| Run script                    | Java | Protocol | Port | Update script                    |
|-------------------------------|------|----------|------|----------------------------------|
| **run-marlo-java17.sh / .bat** | 17   | HTTP     | **8080** | `update-marlo-dev-java17.*`  |
| **run-marlo-java8.sh / .bat**  | 8    | HTTPS    | **8443** | `update-marlo-dev-java8.*`   |

- **Java 17 (default):** URLs in `marlo-dev.properties` are set to **HTTP on port 8080** so that when you open `http://localhost:8080/marlo-web/`, resources load correctly.
- **Java 8:** URLs are set to **HTTPS on port 8443** so that generated links use `https://localhost:8443/marlo-web/`.

Each run script calls the corresponding update script before starting the server so the properties file matches the run you are using.
