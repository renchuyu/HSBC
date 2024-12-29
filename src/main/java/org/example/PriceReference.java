package org.example;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * The type Price reference.
 */
public class PriceReference {

    private String instrument;
    private String type;
    private BigDecimal theoPrice;
    private BigDecimal lastTradePrice;
    private BigDecimal closePrice;

    private PriceReference(String instrument, String type, BigDecimal theoPrice, BigDecimal lastTradePrice, BigDecimal closePrice) {
        this.instrument = instrument;
        this.type = type;
        this.theoPrice = theoPrice;
        this.lastTradePrice = lastTradePrice;
        this.closePrice = closePrice;
    }

    /**
     * Instantiates a new Price reference.
     *
     * @param instrument     the instrument
     * @param type           the type
     * @param theoPrice      the theo price
     * @param lastTradePrice the last trade price
     * @param closePrice     the close price
     */
    public PriceReference(String instrument, String type, float theoPrice, float lastTradePrice, float closePrice) {
        this.instrument = instrument;
        this.type = type;
        this.theoPrice = BigDecimal.valueOf(theoPrice).setScale(2, RoundingMode.HALF_UP);
        this.lastTradePrice = BigDecimal.valueOf(lastTradePrice).setScale(2,RoundingMode.HALF_UP);
        this.closePrice = BigDecimal.valueOf(closePrice).setScale(2,RoundingMode.HALF_UP);
    }

    /**
     * Gets instrument.
     *
     * @return the instrument
     */
    public String getInstrument() {
        return instrument;
    }

    /**
     * Sets instrument.
     *
     * @param instrument the instrument
     */
    public void setInstrument(String instrument) {
        this.instrument = instrument;
    }

    /**
     * Gets type.
     *
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * Sets type.
     *
     * @param type the type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Gets theo price.
     *
     * @return the theo price
     */
    public BigDecimal getTheoPrice() {
        return theoPrice;
    }

    /**
     * Sets theo price.
     *
     * @param theoPrice the theo price
     */
    public void setTheoPrice(BigDecimal theoPrice) {
        this.theoPrice = theoPrice;
    }

    /**
     * Gets last trade price.
     *
     * @return the last trade price
     */
    public BigDecimal getLastTradePrice() {
        return lastTradePrice;
    }

    /**
     * Sets last trade price.
     *
     * @param lastTradePrice the last trade price
     */
    public void setLastTradePrice(BigDecimal lastTradePrice) {
        this.lastTradePrice = lastTradePrice;
    }

    /**
     * Gets close price.
     *
     * @return the close price
     */
    public BigDecimal getClosePrice() {
        return closePrice;
    }

    /**
     * Sets close price.
     *
     * @param closePrice the close price
     */
    public void setClosePrice(BigDecimal closePrice) {
        this.closePrice = closePrice;
    }

    /**
     * Gets reference price.
     *
     * @return the reference price
     * @throws Exception the exception
     */
    public BigDecimal getReferencePrice() throws Exception {
        if (this.lastTradePrice.floatValue() >0)
            return this.lastTradePrice;
        if(this.closePrice.floatValue() >0)
            return this.closePrice;
        if(this.theoPrice.floatValue() >0)
            return this.theoPrice;

        throw new Exception("no reference found for instrument:" + instrument);
    }
}
