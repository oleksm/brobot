package oleks.brobot.exchange

import groovy.util.logging.Slf4j
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

import java.util.concurrent.ConcurrentLinkedQueue

@Slf4j
//@Aspect
//@Component("poloneixApiRateControlAspect")
class PoloneixApiRateControlAspect {

    ConcurrentLinkedQueue queue = new ConcurrentLinkedQueue()
    int limit
    int period

    @Pointcut("execution(@oleks.brobot.exchange.PoloneixApiRateControl * *(..))")
    def synchronized beforeCallingRestClientService() {

        while (!queue.isEmpty()) {
            long elapsed = System.currentTimeMillis() - queue.peek()
            long control = period - elapsed
            // log.debug "beforeCallingRestClientService elapsed: ${elapsed}, control: ${control}"
            if (control <= 0) {
                queue.poll()
            } else {
                if (queue.size() >= limit) {
                    log.info "PoloneixApiRateControl sleep ${control} ms"
                    Thread.sleep(control)
                }
                else {
                    break
                }
            }
        }
        queue.add(System.currentTimeMillis())
        log.debug "PoloneixApiRateControl ${queue.size()}/${limit}"
    }
}
