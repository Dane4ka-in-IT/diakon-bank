docker stop bankdb
docker rm bankdb
docker run --name bankdb -e POSTGRES_PASSWORD=secret -p 5432:5432 -d postgres
timeout 5
docker exec -it bankdb psql -U postgres -c "CREATE DATABASE bankdb"
echo exploded