-- Add closing_balance column to months so we can persist recalculated closing values after ledger changes
ALTER TABLE months
  ADD COLUMN closing_balance DECIMAL(12,2) DEFAULT 0.00;