# docker-liteloaderqqnt

基于 [sgpublic/docker-linuxqq](https://github.com/sgpublic/docker-linuxqq) 构建。

## 食用方法

```yaml
version: '3.1'
services:
  linuxqq:
    image: mhmzx/docker-liteloaderqqnt:v1.2.0
    restart: unless-stopped
    ports:
      - 5800:5800
    volumes:
      - ./config:/home/linuxqq/config
      - ./config/baseimage-gui:/config # 上游项目的配置目录
```

支持修改的环境变量：

| 环境变量                        | 描述	                                                       | 默认值（留空则表示必填）                       |
|-----------------------------|-----------------------------------------------------------|------------------------------------|
| LITELOADERQQNT_PROFILE      | LiteLoaderQQNT 数据文件目录                                     | $XDG_CONFIG_HOME/LiteLoaderProfile |
| LITELOADERQQNT_HOME         | LiteLoaderQQNT 安装目录，若需使用外部 LiteLoaderQQNT 安装实例可修改此目录或直接挂载 | $XDG_CONFIG_HOME/LiteLoaderQQNT    |

其他环境变量请参阅：[sgpublic/docker-linuxqq](https://github.com/jsgpublic/docker-linuxqq?tab=readme-ov-file#食用方法)
