# CachedUDP
A reliable protocol based on UDP, if response packet is lost, client can recover it from server's cache. 

## Packet Structure
Packet ID|
----------
Flag|
Token|
Data|


## Available Flags
Flags| Usage
-----|-----
CU_REQ_CACHE| Request from client, aggreed to use cache.
CU_RSP_CACHE| Response from server, data is from cache.
CU_REQ_NOCAC| Request from client, cache is not allowed in response.
CU_RSP_NOCAC| Response from server, no cache is used.

## How it works
1. Every request send from client has a random token in header.
2. After to request is recieved by server, the response is created and stored in a Hashmap as cache.
3. If the response packet is lossed somehow, the client will request the server again with the same token, and the response will be retrived from the token hashmap in the server side.
4. If the client don't want to recieve the response from the cache, it can change the flag in the packet header to identify.

## Todo
Implement checksum.
