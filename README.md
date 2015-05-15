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
Client                        Server
------>-->--[token]request->--->-->
    -lost<---<--<--response<--<---cachemap<token, response>
----->---->-[token]request->--->-->
<----<-----<----cachedresponse--<--

1. If client is agreed to use cache, the request header is created with a random token.
2. After to request is recieved by server, the response is created and stored in a Hashmap.
3. If the response packet is lossed somehow, the client will request the server again with the same token, and the response will be retrived from the token hashmap in the server side.
4. If the client don't want to recieve the response from the cache, it can change the flag in the packet header to identify.

## Todo
Implement checksum.
