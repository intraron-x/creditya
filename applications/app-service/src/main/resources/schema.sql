CREATE TABLE IF NOT EXISTS userdata (
    id VARCHAR(255) PRIMARY KEY,
    nombres VARCHAR(255) NOT NULL,
    apellidos VARCHAR(255) NOT NULL,
    fecha_nacimiento DATE NOT NULL,
    direccion VARCHAR(255),
    telefono VARCHAR(20),
    correo_electronico VARCHAR(255) NOT NULL,
    salario_base DOUBLE PRECISION NOT NULL
);