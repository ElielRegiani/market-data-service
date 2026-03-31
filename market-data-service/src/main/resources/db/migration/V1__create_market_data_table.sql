CREATE TABLE IF NOT EXISTS market_data (
    id BIGSERIAL PRIMARY KEY,
    symbol VARCHAR(32) NOT NULL,
    price DOUBLE PRECISION NOT NULL,
    volume DOUBLE PRECISION NOT NULL,
    ts TIMESTAMPTZ NOT NULL,
    sma DOUBLE PRECISION NULL,
    ema DOUBLE PRECISION NULL,
    rsi DOUBLE PRECISION NULL,
    volatility DOUBLE PRECISION NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_market_data_symbol_ts
    ON market_data(symbol, ts);

CREATE INDEX IF NOT EXISTS ix_market_data_symbol_ts_desc
    ON market_data(symbol, ts DESC);

