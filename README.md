# noty-server

This project uses Quarkus, the Supersonic Subatomic Java Framework.
If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```
./mvnw -DBASICAUTH_PASSWORD=123 clean quarkus:dev
```

## Testing in Java Virtual Machine mode

```
./mvnw -Ptest -Dquarkus.profile=db-in-docker clean verify
```

## Testing the native executable 

```
./mvnw -Pnative clean verify
```

## Packaging and running the application

The application can be packaged using 
```
./mvnw -DBASICAUTH_PASSWORD=123 -DskipTests package
```
It produces the `noty-server-quarkus-1.0-SNAPSHOT-runner.jar` file in the `/target` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/lib` directory.

The application is runnable using 
```
java -jar target/noty-server-quarkus-1.0-SNAPSHOT-runner.jar
```

## Creating a native executable

You can create a native executable using: 
```
./mvnw package -DskipTests -DBASICAUTH_PASSWORD=123 -Pnative
```

You can then execute your native executable with: 
`./target/noty-server-quarkus-1.0-SNAPSHOT-runner`
