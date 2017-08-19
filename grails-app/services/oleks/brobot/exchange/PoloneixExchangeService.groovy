package oleks.brobot.exchange

import grails.gorm.transactions.Transactional

@Transactional
class PoloneixExchangeService extends ExchangeService {

    def restClientService

    String returnTickerUrl = "https://poloniex.com/public?command=returnTicker"
    String return24hVolumeUrl = "https://poloniex.com/public?command=return24hVolume"
    String returnOrderBookUrl = "https://poloniex.com/public?command=returnOrderBook&currencyPair={0}&depth={1}"
    String returnTradeHistoryUrl = "https://poloniex.com/public?command=returnTradeHistory&currencyPair={0}&start={1}&end={2}"
    String returnChartDataUrl = "https://poloniex.com/public?command=returnChartData&currencyPair={1}&start={2}&end={3}&period={4}"
    String returnCurrenciesUrl = "https://poloniex.com/public?command=returnCurrencies"
    String returnLoanOrdersUrl = "https://poloniex.com/public?command=returnLoanOrders&currency={1}"
    String tradingApiUrl = "https://poloniex.com/tradingApi"


    /**
     * Returns the ticker for all markets. Sample output:
     * {"BTC_LTC":{"last":"0.0251","lowestAsk":"0.02589999","highestBid":"0.0251","percentChange":"0.02390438",
     * "baseVolume":"6.16485315","quoteVolume":"245.82513926"},"BTC_NXT":{"last":"0.00005730","lowestAsk":"0.00005710",
     * "highestBid":"0.00004903","percentChange":"0.16701570","baseVolume":"0.45347489","quoteVolume":"9094"}, ... }
     * Call: https://poloniex.com/public?command=returnTicker
     * @return
     */
    @PoloneixApiRateControl
    def returnTicker() {
        def resp = restClientService.get(returnTickerUrl)
        def result = resp?.json
        if (result) {
            log.debug "returnTicker response: ${result}"
            result
        }
    }

    /**
     * Returns information about currencies. Sample output:
     * {"1CR":{"maxDailyWithdrawal":10000,"txFee":0.01,"minConf":3,"disabled":0},"ABY":{"maxDailyWithdrawal":10000000,"txFee":0.01,"minConf":8,"disabled":0}, ... }
     * Call: https://poloniex.com/public?command=returnCurrencies
     */
    @PoloneixApiRateControl
    def returnCurrencies() {
        def resp = restClientService.get(returnCurrenciesUrl)
        def result = resp?.json
        if (result) {
            log.debug "returnCurrencies response: ${result}"
            // Filter out delisted, frozen and disabled currencies
            result.findAll { it.value["delisted"] == 0 && it.value["frozen"] == 0 && it.value["disabled"] == 0}
        }
    }

    /**
     * Returns all of your available balances. Sample output:
     * {"BTC":"0.59098578","LTC":"3.31117268", ... }
     * @param account
     * @return
     */
    @PoloneixApiRateControl
    def returnBalances(Account account) {

        def exchange = account.exchange
        def params = ["command": "returnBalances"]
        def resp = restClientService.securePost(tradingApiUrl, account.apiKey, account.apiSecret, exchange.signatureMethod, params)
        def result = resp?.json
        if (result) {
            log.debug "returnBalances response: ${result} "
            result
        }
    }

    /**
     * Places a limit buy order in a given market. Required POST parameters are "currencyPair", "rate", and "amount".
     * If successful, the method will return the order number. Sample output:
     * {"orderNumber":31226040,"resultingTrades":[{"amount":"338.8732","date":"2014-10-18 23:03:21","rate":"0.00000173","total":"0.00058625","tradeID":"16164","type":"buy"}]}
     * You may optionally set "fillOrKill", "immediateOrCancel", "postOnly" to 1. A fill-or-kill order will either fill
     * in its entirety or be completely aborted. An immediate-or-cancel order can be partially or completely filled, but
     * any portion of the order that cannot be filled immediately will be canceled rather than left on the order book.
     * A post-only order will only be placed if no portion of it fills immediately; this guarantees you will never pay
     * the taker fee on any part of the order that fills.
     * @param account
     */
    @PoloneixApiRateControl
    def buy (Account account, String pair, double rate, double amount) {
        def exchange = account.exchange
        log.info "buy ${exchange.name}:${account.id} pair: ${pair}, rate: ${rate}, amount: ${amount}"
        def params = ["command": "buy",
                      "immediateOrCancel": 1, // do not leave orders open, too complex to handle for mvp. Maybe will do it later
                      "currencyPair": pair,
                      "rate": rate,
                      "amount": amount]
        def resp = restClientService.securePost(tradingApiUrl, account.apiKey, account.apiSecret, exchange.signatureMethod, params)
        def result = resp?.json
        if (result) {
            log.debug "buy response: ${result} "
        }
        result
    }
}
