# hw4

* монолит
* реактивщина
* без ОРМ
* добавлены индексы к полям для поиска
* добавлено кеширование ленты друзей

### install

mvn clean install
docker build -f docker/Dockerfile.jvm -t otus-highload-hw4:latest .
docker images

### publish

docker tag otus-highload-hw4:latest recvezitor/otus-highload-hw4:latest
docker login -> recvezitor/password
docker push recvezitor/otus-highload-hw4:latest

### deploy 

cd docker
docker compose up

### Инструкция

1. Залогиниться под Леопольдом
2. Получить посты друзей Леопольда первый раз - пойдем в БД и сохраним в кеш
3. Получить посты друзей Леопольда второй раз - берем из кеша
4. Логинимся под Леди Гагой
5. Публикуем пост под Леди Гагой - у Леопольда будет инвалидация кеша
