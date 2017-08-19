package oleks.brobot.exchange

class Account {

    String apiKey
    String apiSecret
    Double holdAmountBtc
    Double maxTradeUsd
    Double minTradeUsd

    static belongsTo = [exchange: Exchange]

    static constraints = {
        apiKey nullable: false, blank: false
        apiSecret nullable: false, blank: false
        exchange  nullable: false, blank: false
        holdAmountBtc nullable: false, blank: false, min: 0.0d
        minTradeUsd nullable: false, blank: false, min: 0.0d
        maxTradeUsd nullable: false, blank: false, min: 0.0d
    }
}
