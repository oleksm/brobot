package oleks.brobot.exchange

import grails.gorm.transactions.Transactional

@Transactional
class AccountService {
    def exchangeService

    def returnBalances(Account account) {
        exchangeService.getService(account.exchange)
    }
}
