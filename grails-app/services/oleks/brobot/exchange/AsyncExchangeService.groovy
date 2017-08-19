package oleks.brobot.exchange

import grails.async.DelegateAsync
import grails.gorm.transactions.Transactional

@Transactional
class AsyncExchangeService {
    @DelegateAsync ExchangeService exchangeService
}
