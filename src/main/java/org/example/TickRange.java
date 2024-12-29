package org.example;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * The type Tick range.
 */
public class TickRange  implements Comparable<TickRange>{
    private BigDecimal minPrice;
    private BigDecimal maxPrice;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TickRange tickRange = (TickRange) o;
        //only compare the minPrice
        return minPrice.compareTo(tickRange.minPrice) == 0;
    }

    @Override
    public int hashCode() {
        //only hash the minPrice to make sure once a equal b , then a and b has the same hash
        return Objects.hash(minPrice);
    }

    /**
     * Instantiates a new Tick range.
     *
     * @param minPrice the min price
     * @param maxPrice the max price
     */
    public TickRange(BigDecimal minPrice, BigDecimal maxPrice) {
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
    }

    /**
     * Gets min price.
     *
     * @return the min price
     */
    public BigDecimal getMinPrice() {
        return minPrice;
    }

    /**
     * Sets min price.
     *
     * @param minPrice the min price
     */
    public void setMinPrice(BigDecimal minPrice) {
        this.minPrice = minPrice;
    }

    /**
     * Gets max price.
     *
     * @return the max price
     */
    public BigDecimal getMaxPrice() {
        return maxPrice;
    }

    /**
     * Sets max price.
     *
     * @param maxPrice the max price
     */
    public void setMaxPrice(BigDecimal maxPrice) {
        this.maxPrice = maxPrice;
    }

    @Override
    public int compareTo(TickRange tickRange) {
        // only compare the minPrice
        return this.minPrice.compareTo(tickRange.getMinPrice());
    }

}
