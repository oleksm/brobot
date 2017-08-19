package oleks.brobot.exchange

class Exchange {

    String name
    String signatureMethod

    static hasMany = [accounts: Account]

    static constraints = {

    }
}
