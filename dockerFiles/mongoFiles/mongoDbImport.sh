#!/bin/bash
tar -xvzf /opt/mongoDbData/Ophanim2MongoDb.tar.gz -C /opt/mongoDbData/
mongorestore --host localhost:27017 --db Ophanim2 /opt/mongoDbData/Ophanim2
