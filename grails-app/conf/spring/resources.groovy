import oleks.brobot.RestClientService
import oleks.brobot.exchange.BrokerService
import oleks.brobot.exchange.PoloneixApiRateControlAspect
import org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator

// Place your Spring DSL code here
beans = {
    restClientService(RestClientService) {
        userAgent = "BROBOT"
        connectionTimeout = 500
        readTimeout = 5000
    }

    poloneixApiRateControlAspect(PoloneixApiRateControlAspect) {
        limit = 2
        period = 1000 // ms
    }

    xmlns aop:"http://www.springframework.org/schema/aop"
    aop {
        config("proxy-target-class": true) {
            aspect(ref: "poloneixApiRateControlAspect") {
                before method: "beforeCallingRestClientService",
                        pointcut: "execution(@oleks.brobot.exchange.PoloneixApiRateControl * *(..))"
            }
        }
    }
}
