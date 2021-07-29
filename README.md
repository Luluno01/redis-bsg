# Redis-BSG

Redis storage backend for [BungeeSafeguard](https://github.com/Luluno01/BungeeSafeguard).

Tested on Waterfall, version `git:Waterfall-Bootstrap:1.17-R0.1-SNAPSHOT:93773f9:448`.

- [Redis-BSG](#redis-bsg)
  - [Feature](#feature)
  - [Configuration](#configuration)
  - [Commands](#commands)
    - [Main Command](#main-command)
      - [redis-bsg reload](#redis-bsg-reload)

## Feature

Store the whitelist/blacklist using Redis.

## Configuration

```YAML
url: redis://localhost:6379  # The Redis URL
```

## Commands

### Main Command

Alias: `redisbsg`.

#### redis-bsg reload

Reload configuration (from file `plugins/Redis-BSG/config.yml`) and reconnect to the Redis store:

```
redis-bsg reload
```
