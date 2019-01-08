local key = KEYS[1]
local ttl = ARGV[1]
local result = redis.call('setnx', key, KEYS[2])
if result == 1 then redis.call('pexpire', key, ttl) end return result