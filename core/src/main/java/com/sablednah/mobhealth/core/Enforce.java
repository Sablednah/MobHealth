package com.sablednah.mobhealth.core;

/** A server-side override for a client boolean option. */
public enum Enforce {
    /** Let the client decide (no override). */
    CLIENT,
    /** Force the option on for all clients. */
    ON,
    /** Force the option off for all clients. */
    OFF;

    /** {@code null} = client decides; otherwise the forced value. */
    public Boolean asOverride() {
        return this == CLIENT ? null : (this == ON ? Boolean.TRUE : Boolean.FALSE);
    }
}
