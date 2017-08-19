package oleks.brobot

import grails.gorm.transactions.Transactional
import grails.plugins.rest.client.RestBuilder

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import java.util.concurrent.atomic.AtomicLong

@Transactional
class RestClientService {
    static transactional = false

    String userAgent
    long connectionTimeout
    long readTimeout

    AtomicLong nonceGen = new AtomicLong(System.currentTimeMillis())

    def get(def url) {
        log.debug "GET ${url} with useragent ${userAgent}"
        long start = System.currentTimeMillis()
        def resp = new RestBuilder(connectTimeout: connectionTimeout, readTimeout: readTimeout).get(url) {
            header 'User-Agent', "${userAgent}"
        }
        log.debug "GET responded within ${System.currentTimeMillis() - start} ms"
        if (resp.status != 200) {
            log.warn "rest call responded with status ${resp.status}: '${url}'\n resp.responseEntity.body"
        }
        return resp
    }

    def securePost(def url, def key, def secret, def algorithm, def params = [:]) {
        // generate nonce
        def nonce = nonceGen.incrementAndGet()
        // final params list
        def form = [nonce: nonce]
        if (params) {
            form.putAll(params)
        }
        // to a query string
        def query = form.collect{k,v -> "$k=$v"}.join('&')
        // call resource
        log.debug "SECURE POST ${url} with useragent ${userAgent} and query: ${query}"
        long start = System.currentTimeMillis()
        def resp = new RestBuilder(connectTimeout: connectionTimeout, readTimeout: readTimeout).post(url) {
            header 'User-Agent', "${userAgent}"
            header 'Content-Type', 'application/x-www-form-urlencoded'
            header 'Key', key
            header 'Sign', sign(query, secret, algorithm)
            body query
        }
        log.debug "POST responded within ${System.currentTimeMillis() - start} ms"
        if (resp.status != 200) {
            log.error "rest call responded with ${resp.status}: '${url}'\n resp.responseEntity.body"
        }
        return resp
    }

    def securePostDummy(def url, def key, def secret, def algorithm, def params = [:]) {
        log.debug "securePostDummy url: ${url}"
    }

    def sign(String query, def secret, def algorithm) {
        def keySpec = new SecretKeySpec(secret.bytes, algorithm)
        def mac = Mac.getInstance(algorithm)
        mac.init(keySpec)
        def bytes = mac.doFinal(query.bytes)
        return bytes.encodeHex().toString()
    }

    def invokeMethod(String name, args) {
        if (active) {
            return InvokeUtils.invokeMethod(this, name, args)
        }
    }

}
