# booster-server

The REST API exposing operations to interact with the `vocabulary entries` and `notes`. 

No concept of a user is supported for now, meaning that the system treats all requests as generic and does not differentiate between the requests issued by different users.

### To start the application locally:
* Start the PostgreSQL DB by running the following command from the project root:
```bash
docker-compose up -d 
```
* Start the app either from the IDEA or by running the following command from the project root:
```bash
./mvnw clean install && java -jar target/server-0.0.1-SNAPSHOT.jar
```

**Important**. For now, all migration scripts are added into the `init.sql` directly and the concept of the migration changelog is not respected. This means that you might pull the latest changes affecting the `init.sql` checksum which prevents the app from starting. If this happens, simply recreate the DB by running the following command from the project root:
```bash
docker-compose down && docker-compose up -d
```
This is acceptable for development only. Once the app is mature enough - the correct approach of migration scripts will be used, and new DB changes will be located in the new `.sql` files.
