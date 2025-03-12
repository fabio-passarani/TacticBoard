-- Create subscription plans table
CREATE TABLE subscription_plans (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    duration_months INTEGER NOT NULL,
    max_teams INTEGER,
    max_players_per_team INTEGER,
    max_storage_mb INTEGER,
    features JSONB,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create user subscriptions table
CREATE TABLE user_subscriptions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    plan_id BIGINT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL,
    payment_method VARCHAR(50),
    payment_reference VARCHAR(100),
    auto_renew BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (plan_id) REFERENCES subscription_plans (id)
);

-- Create payment history table
CREATE TABLE payment_history (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    subscription_id BIGINT,
    amount DECIMAL(10, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    payment_date TIMESTAMP NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    payment_reference VARCHAR(100),
    status VARCHAR(20) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (subscription_id) REFERENCES user_subscriptions (id) ON DELETE SET NULL
);

-- Insert default subscription plans
INSERT INTO subscription_plans (name, description, price, duration_months, max_teams, max_players_per_team, max_storage_mb, features, active)
VALUES 
('Free', 'Basic plan for individual coaches', 0.00, 0, 1, 20, 100, '{"customTactics": false, "advancedAnalytics": false, "exportPdf": false}', true),
('Premium', 'Advanced plan for serious coaches', 9.99, 1, 5, 30, 500, '{"customTactics": true, "advancedAnalytics": true, "exportPdf": true}', true),
('Team', 'Complete solution for clubs and organizations', 29.99, 1, 20, 50, 2000, '{"customTactics": true, "advancedAnalytics": true, "exportPdf": true, "multipleCoaches": true, "prioritySupport": true}', true);

-- Create indexes
CREATE INDEX idx_user_subscriptions_user ON user_subscriptions (user_id);
CREATE INDEX idx_user_subscriptions_plan ON user_subscriptions (plan_id);
CREATE INDEX idx_user_subscriptions_dates ON user_subscriptions (start_date, end_date);
CREATE INDEX idx_payment_history_user ON payment_history (user_id);
CREATE INDEX idx_payment_history_subscription ON payment_history (subscription_id);
CREATE INDEX idx_payment_history_date ON payment_history (payment_date);