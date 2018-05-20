package com.ximcoin.ximwallet;

/**
 * Constants used in Staging.
 */
public class EnvironmentConstants {
    public static final String HORIZON_API_ENDPOINT = "https://horizon-testnet.stellar.org";
    public static final String STELLAR_TERM_API_ENDPOINT = "https://api.stellarterm.com/v1/";
    public static final boolean IS_PRODUCTION = false;
    public static final double BASE_FEE_PRECISION = 0.0000001f; // 1 Stroop
    public static final String LUMENAUT_INFLATION_ADDRESS = "GCCD6AJOYZCUAQLX32ZJF2MKFFAUJ53PVCFQI3RHWKL3V47QYE2BNAUT";
    public static final String STELLAR_EXPLORER_URL_PREFIX = "https://testnet.steexp.com/tx/";
    //TODO: Xim wallet support email (debug)
    public static final String CONTACT_EMAIL = "support+debug@ximcoin.com";
    public static final String XIM_ISSUER = "GAPVNGRE2SIKV7QMMGZZH24JL6CSGSNBS64V654VZ2MK2RXAOEV5LF3S";
    // TODO: This is likely to be different than the prod one?
    public static final String EXPORT_ID_LOGIN_URL = "https://exportid.com/xim/add_account.html?pk=";
}