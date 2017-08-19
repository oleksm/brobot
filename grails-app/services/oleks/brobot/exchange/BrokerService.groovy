package oleks.brobot.exchange

import grails.gorm.transactions.Transactional

@Transactional
class BrokerService {

    def exchangeService
    def asyncExchangeService

    /**
     * Algorithm
     * 1. + Pull account balance
     * 2. + If there some BTC
     * 3. + Pull available currencies
     * 4. Split balance to x currencies, with respect of minumum amount to purchase (configurable)
     * 5. Buy Currencies
     * @param account
     */
    def handleTrade(Account account) {
        def exchange = account.exchange
        def balances = exchangeService.returnBalances(account)
        double balanceBtc = Double.valueOf(balances.BTC)
        double availableBtc = balanceBtc - account.holdAmountBtc
        log.info "handleCustomer: account ${exchange.name}:${account.id}, balanceBtc: ${balanceBtc}, availableBtc: ${availableBtc}"
        if (availableBtc > 0) {
            def currencies = exchangeService.returnCurrencies(exchange)
            assert currencies.size() > 0
            def tickers = exchangeService.returnTicker(exchange)
            assert tickers.size() > 0
            double exchangeRateUsd = Double.valueOf(tickers["USDT_BTC"].last)
            assert exchangeRateUsd > 0
            double availableUsd = availableBtc * exchangeRateUsd
            double tradeUsd = availableUsd > account.maxTradeUsd ? account.maxTradeUsd : availableUsd
            log.debug "handleCustomer exchangeRateUsd: ${exchangeRateUsd}, availableUsd: ${availableUsd}, tradeUsd: ${tradeUsd}"
            if (tradeUsd >= account.minTradeUsd) {
                double transactionBtc = tradeUsd / exchangeRateUsd / (double)currencies.size()
                currencies.each { currency ->
                    def pair = "BTC_${currency.key}"
                    def ticker = tickers[pair]
                    if (ticker) {
                        def rate = Double.valueOf(ticker.lowestAsk)
                        assert rate > 0
                        def amount = transactionBtc / rate
                        asyncExchangeService.buy(account, pair, rate, amount)
                    }
                    else {
                        log.debug "no ticker to trade: ${pair}"
                    }
                }
            }
        }
    }

    def handleTradeSmall(Account account) {
        def exchange = account.exchange
        def balances = exchangeService.returnBalances(account)
        double balanceBtc = Double.valueOf(balances.BTC)
        double availableBtc = balanceBtc - account.holdAmountBtc
        log.info "handleCustomer: account ${exchange.name}:${account.id}, balanceBtc: ${balanceBtc}, availableBtc: ${availableBtc}"
        if (availableBtc > 0) {
            def currencies = exchangeService.returnCurrencies(exchange)
            assert currencies.size() > 0
            def tickers = exchangeService.returnTicker(exchange)
            assert tickers.size() > 0
            double exchangeRateUsd = Double.valueOf(tickers["USDT_BTC"].last)
            assert exchangeRateUsd > 0
            double availableUsd = availableBtc * exchangeRateUsd
            double tradeUsd = availableUsd > account.maxTradeUsd ? account.maxTradeUsd : availableUsd
            log.debug "handleCustomer exchangeRateUsd: ${exchangeRateUsd}, availableUsd: ${availableUsd}, tradeUsd: ${tradeUsd}"
            if (tradeUsd >= account.minTradeUsd) {
                double transactionBtc = tradeUsd / exchangeRateUsd / (double)currencies.size()
                log.debug "transactionBtc: ${transactionBtc}"
                def pair = "BTC_STR"
                def ticker = tickers[pair]
                if (ticker) {
                    def rate = Double.valueOf(ticker.lowestAsk)
                    assert rate > 0
                    def amount = transactionBtc / rate
                    asyncExchangeService.buy(account, pair, rate, amount)
                }
                else {
                    log.debug "no ticker to trade: ${pair}"
                }
            }
        }
    }
}
