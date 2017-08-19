package oleks.brobot.exchange

class Ticker {

    String pair
    String refId
    Double fee

    static constraints = {
        pair nullable: false, blank: false
        pair nullable: false, blank: false
        fee blank: false
    }
}
