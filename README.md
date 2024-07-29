# docker-liteloaderqqnt

基于 [sgpublic/docker-linuxqq](https://github.com/sgpublic/docker-linuxqq) 构建。

## 食用方法

```yaml
version: '3.1'
services:
  linuxqq:
    image: mhmzx/docker-liteloaderqqnt:v3.2.10_240715-6-v1.1.2-3
    restart: unless-stopped
    ports:
      - 5800:5800
    volumes:
      - /path/to/config:/home/linuxqq/config
    # mac_address: XX:XX:XX:XX:XX:XX # 指定一个 mac 地址以支持自动登陆（TODO）
```

支持的环境变量请参阅：[jlesage/docker-baseimage-gui#environment-variables](https://github.com/jlesage/docker-baseimage-gui?tab=readme-ov-file#environment-variables)
