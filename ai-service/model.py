"""
Advanced AI Credit Scoring & Risk Prediction Model
Uses ensemble of Random Forest, Gradient Boosting, and Logistic Regression
with feature engineering pipeline for wallet risk assessment.
"""

import numpy as np
import pandas as pd
from sklearn.ensemble import RandomForestClassifier, GradientBoostingClassifier, VotingClassifier
from sklearn.linear_model import LogisticRegression
from sklearn.preprocessing import StandardScaler
from sklearn.model_selection import train_test_split
from sklearn.metrics import accuracy_score, classification_report, roc_auc_score
import joblib
import os
import json
from datetime import datetime


class WalletRiskModel:
    """Ensemble AI model for wallet credit scoring and default prediction."""

    def __init__(self):
        self.scaler = StandardScaler()
        self.ensemble_model = None
        self.is_trained = False
        self.training_date = None
        self.model_version = "2.0.0"
        self.feature_names = [
            'wallet_balance', 'total_asset_value', 'total_token_value',
            'total_transactions', 'late_payments', 'repayment_rate',
            'asset_coverage_ratio', 'avg_transaction_amount',
            'transaction_frequency', 'balance_volatility',
            'loyalty_points', 'account_age_days'
        ]
        self._build_model()

    def _build_model(self):
        """Build ensemble model with 3 classifiers."""
        rf = RandomForestClassifier(
            n_estimators=100, max_depth=10, min_samples_split=5,
            random_state=42, n_jobs=-1
        )
        gb = GradientBoostingClassifier(
            n_estimators=100, max_depth=5, learning_rate=0.1,
            random_state=42
        )
        lr = LogisticRegression(
            max_iter=1000, C=1.0, random_state=42
        )
        self.ensemble_model = VotingClassifier(
            estimators=[('rf', rf), ('gb', gb), ('lr', lr)],
            voting='soft',
            weights=[3, 2, 1]
        )

    def generate_synthetic_data(self, n_samples=5000):
        """Generate realistic synthetic training data for wallet risk."""
        np.random.seed(42)

        # Generate features
        wallet_balance = np.random.exponential(5000, n_samples)
        total_asset_value = np.random.exponential(50000, n_samples)
        total_token_value = np.random.exponential(2000, n_samples)
        total_transactions = np.random.poisson(20, n_samples).astype(float)
        late_payments = np.random.poisson(1, n_samples).astype(float)
        repayment_rate = np.clip(np.random.beta(5, 2, n_samples), 0, 1)
        asset_coverage_ratio = np.where(
            wallet_balance > 0, total_asset_value / wallet_balance, 0
        )
        avg_transaction_amount = np.random.exponential(500, n_samples)
        transaction_frequency = np.random.exponential(5, n_samples)
        balance_volatility = np.random.exponential(0.3, n_samples)
        loyalty_points = np.random.poisson(500, n_samples).astype(float)
        account_age_days = np.random.exponential(365, n_samples)

        # Generate target (default = 1, no default = 0)
        # Higher balance, assets, repayment → lower default risk
        default_prob = 1 / (1 + np.exp(
            -(-2.0
              + 0.0001 * wallet_balance * -1
              + 0.00001 * total_asset_value * -1
              + late_payments * 0.5
              + repayment_rate * -3.0
              + balance_volatility * 2.0
              + total_transactions * -0.02
              + np.random.normal(0, 0.5, n_samples))
        ))
        target = (default_prob > 0.5).astype(int)

        X = pd.DataFrame({
            'wallet_balance': wallet_balance,
            'total_asset_value': total_asset_value,
            'total_token_value': total_token_value,
            'total_transactions': total_transactions,
            'late_payments': late_payments,
            'repayment_rate': repayment_rate,
            'asset_coverage_ratio': asset_coverage_ratio,
            'avg_transaction_amount': avg_transaction_amount,
            'transaction_frequency': transaction_frequency,
            'balance_volatility': balance_volatility,
            'loyalty_points': loyalty_points,
            'account_age_days': account_age_days,
        })

        return X, pd.Series(target, name='default')

    def train(self, X=None, y=None):
        """Train the ensemble model. Uses synthetic data if none provided."""
        if X is None or y is None:
            X, y = self.generate_synthetic_data()

        X_train, X_test, y_train, y_test = train_test_split(
            X, y, test_size=0.2, random_state=42, stratify=y
        )

        # Scale features
        X_train_scaled = self.scaler.fit_transform(X_train)
        X_test_scaled = self.scaler.transform(X_test)

        # Train ensemble
        self.ensemble_model.fit(X_train_scaled, y_train)
        self.is_trained = True
        self.training_date = datetime.now().isoformat()

        # Evaluate
        y_pred = self.ensemble_model.predict(X_test_scaled)
        y_proba = self.ensemble_model.predict_proba(X_test_scaled)[:, 1]

        accuracy = accuracy_score(y_test, y_pred)
        auc = roc_auc_score(y_test, y_proba)
        report = classification_report(y_test, y_pred, output_dict=True)

        # Feature importance (from Random Forest)
        rf_model = self.ensemble_model.named_estimators_['rf']
        feature_importance = dict(zip(
            self.feature_names,
            rf_model.feature_importances_.tolist()
        ))

        return {
            'accuracy': round(accuracy, 4),
            'auc_roc': round(auc, 4),
            'classification_report': report,
            'feature_importance': feature_importance,
            'training_samples': len(X_train),
            'test_samples': len(X_test),
            'model_version': self.model_version,
            'training_date': self.training_date
        }

    def predict(self, features):
        """Predict default probability and risk classification."""
        if not self.is_trained:
            self.train()

        feature_array = self._prepare_features(features)
        scaled = self.scaler.transform(feature_array)

        # Get probabilities from ensemble
        proba = self.ensemble_model.predict_proba(scaled)[0]
        default_probability = float(proba[1])

        # Get individual model predictions
        rf_proba = self.ensemble_model.named_estimators_['rf'].predict_proba(scaled)[0][1]
        gb_proba = self.ensemble_model.named_estimators_['gb'].predict_proba(scaled)[0][1]
        lr_proba = self.ensemble_model.named_estimators_['lr'].predict_proba(scaled)[0][1]

        # Credit score calculation (300-850)
        credit_score = self._calculate_credit_score(features, default_probability)

        # Risk classification
        risk_level = self._classify_risk(credit_score, default_probability)

        # Recommendation
        recommendation = self._generate_recommendation(risk_level, credit_score, default_probability)

        # Max allowed transaction
        max_transaction = self._calculate_max_transaction(
            credit_score,
            features.get('wallet_balance', 0),
            features.get('total_asset_value', 0)
        )

        return {
            'credit_score': credit_score,
            'default_probability': round(default_probability, 4),
            'risk_level': risk_level,
            'recommendation': recommendation,
            'max_allowed_transaction': round(max_transaction, 2),
            'model_predictions': {
                'ensemble': round(default_probability, 4),
                'random_forest': round(float(rf_proba), 4),
                'gradient_boosting': round(float(gb_proba), 4),
                'logistic_regression': round(float(lr_proba), 4)
            },
            'input_features': features,
            'model_version': self.model_version,
            'prediction_date': datetime.now().isoformat()
        }

    def batch_predict(self, features_list):
        """Predict for multiple customers at once."""
        results = []
        for features in features_list:
            results.append(self.predict(features))
        return results

    def explain_prediction(self, features):
        """Provide detailed explanation of prediction factors."""
        if not self.is_trained:
            self.train()

        prediction = self.predict(features)

        # Feature contributions (using RF feature importance as proxy)
        rf_model = self.ensemble_model.named_estimators_['rf']
        importances = rf_model.feature_importances_

        feature_array = self._prepare_features(features)
        scaled = self.scaler.transform(feature_array)[0]

        contributions = {}
        for i, name in enumerate(self.feature_names):
            impact = importances[i] * abs(scaled[i])
            direction = "increases" if scaled[i] > 0 else "decreases"
            contributions[name] = {
                'value': features.get(name, 0),
                'importance': round(float(importances[i]), 4),
                'scaled_value': round(float(scaled[i]), 4),
                'impact_score': round(float(impact), 4),
                'effect': f"{direction} risk"
            }

        # Sort by impact
        sorted_contributions = dict(sorted(
            contributions.items(),
            key=lambda x: x[1]['impact_score'],
            reverse=True
        ))

        # Top risk factors
        top_factors = []
        for name, info in list(sorted_contributions.items())[:5]:
            top_factors.append(f"{name}: {info['effect']} (importance: {info['importance']})")

        return {
            **prediction,
            'feature_contributions': sorted_contributions,
            'top_risk_factors': top_factors,
            'explanation': self._generate_explanation(features, prediction)
        }

    def save_model(self, path='models/'):
        """Save trained model to disk."""
        os.makedirs(path, exist_ok=True)
        joblib.dump(self.ensemble_model, os.path.join(path, 'ensemble_model.pkl'))
        joblib.dump(self.scaler, os.path.join(path, 'scaler.pkl'))
        meta = {
            'model_version': self.model_version,
            'training_date': self.training_date,
            'feature_names': self.feature_names,
            'is_trained': self.is_trained
        }
        with open(os.path.join(path, 'model_meta.json'), 'w') as f:
            json.dump(meta, f)

    def load_model(self, path='models/'):
        """Load trained model from disk."""
        model_path = os.path.join(path, 'ensemble_model.pkl')
        scaler_path = os.path.join(path, 'scaler.pkl')
        if os.path.exists(model_path) and os.path.exists(scaler_path):
            self.ensemble_model = joblib.load(model_path)
            self.scaler = joblib.load(scaler_path)
            self.is_trained = True
            with open(os.path.join(path, 'model_meta.json'), 'r') as f:
                meta = json.load(f)
                self.training_date = meta.get('training_date')
            return True
        return False

    # ==================== PRIVATE METHODS ====================

    def _prepare_features(self, features):
        """Convert feature dict to numpy array in correct order."""
        values = []
        for name in self.feature_names:
            values.append(features.get(name, 0))
        return np.array([values])

    def _calculate_credit_score(self, features, default_prob):
        """Calculate FICO-like credit score (300-850)."""
        base = 500
        balance_factor = min(features.get('wallet_balance', 0) / 1000, 1.0) * 100
        asset_factor = min(features.get('asset_coverage_ratio', 0), 2.0) * 50
        tx_factor = min(features.get('total_transactions', 0) / 50, 1.0) * 80
        late_penalty = min(features.get('late_payments', 0) * 40, 200)
        repay_bonus = features.get('repayment_rate', 0) * 70
        loyalty_bonus = min(features.get('loyalty_points', 0) / 1000, 1.0) * 30

        score = (base
                 + balance_factor * 0.20
                 + asset_factor * 0.20
                 + tx_factor * 0.15
                 - late_penalty * 0.20
                 + repay_bonus * 0.15
                 + loyalty_bonus * 0.10
                 - default_prob * 100)

        return int(max(300, min(850, score)))

    def _classify_risk(self, credit_score, default_prob):
        if credit_score >= 750 and default_prob < 0.1:
            return "LOW"
        elif credit_score >= 650 and default_prob < 0.3:
            return "MEDIUM"
        elif credit_score >= 500 and default_prob < 0.6:
            return "HIGH"
        else:
            return "CRITICAL"

    def _generate_recommendation(self, risk_level, credit_score, default_prob):
        recs = {
            "LOW": f"APPROVE - Excellent profile. Score: {credit_score}. Default prob: {default_prob:.1%}",
            "MEDIUM": f"REVIEW - Moderate risk. Score: {credit_score}. Additional verification recommended.",
            "HIGH": f"DENY - High risk. Score: {credit_score}. Default prob: {default_prob:.1%}. Require collateral.",
            "CRITICAL": f"DENY - Critical risk. Score: {credit_score}. Account suspension recommended."
        }
        return recs.get(risk_level, "REVIEW - Unable to assess.")

    def _calculate_max_transaction(self, credit_score, balance, asset_value):
        multipliers = {750: 2.0, 650: 1.0, 500: 0.5}
        multiplier = 0.2
        for threshold, mult in sorted(multipliers.items(), reverse=True):
            if credit_score >= threshold:
                multiplier = mult
                break
        return (balance + asset_value * 0.1) * multiplier

    def _generate_explanation(self, features, prediction):
        """Generate human-readable explanation."""
        risk = prediction['risk_level']
        score = prediction['credit_score']
        prob = prediction['default_probability']

        lines = [f"Risk Assessment: {risk} (Score: {score}/850, Default Probability: {prob:.1%})"]

        if features.get('wallet_balance', 0) > 10000:
            lines.append("✅ Strong wallet balance indicates financial stability.")
        elif features.get('wallet_balance', 0) < 1000:
            lines.append("⚠️ Low wallet balance is a risk factor.")

        if features.get('total_asset_value', 0) > 50000:
            lines.append("✅ High asset value provides good collateral coverage.")

        if features.get('late_payments', 0) > 2:
            lines.append(f"❌ {int(features['late_payments'])} late payments negatively impact the score.")

        if features.get('repayment_rate', 0) > 0.8:
            lines.append("✅ Excellent repayment rate.")
        elif features.get('repayment_rate', 0) < 0.5:
            lines.append("⚠️ Low repayment rate is concerning.")

        if features.get('loyalty_points', 0) > 2000:
            lines.append("✅ High loyalty indicates long-term customer engagement.")

        return "\n".join(lines)
