"""
Equa AI Risk Scoring Microservice
Flask REST API for advanced credit scoring and default prediction.
Uses ensemble ML model (Random Forest + Gradient Boosting + Logistic Regression).
"""

from flask import Flask, request, jsonify
from flask_cors import CORS
from model import WalletRiskModel
from datetime import datetime
import os
import threading

app = Flask(__name__)
CORS(app)

# Initialize and train model on startup
model = WalletRiskModel()
print("🤖 Training AI model on startup...")
training_result = model.train()
print(f"✅ Model trained - Accuracy: {training_result['accuracy']}, AUC: {training_result['auc_roc']}")
model.save_model()


def register_with_eureka():
    """Register this service with Eureka Discovery Server."""
    try:
        import py_eureka_client.eureka_client as eureka_client
        eureka_url = os.environ.get('EUREKA_SERVER_URL', 'http://localhost:8761/eureka/')
        app_name = os.environ.get('APP_NAME', 'AI-SERVICE')
        instance_host = os.environ.get('INSTANCE_HOST', 'localhost')
        instance_port = int(os.environ.get('INSTANCE_PORT', '5000'))

        eureka_client.init(
            eureka_server=eureka_url,
            app_name=app_name,
            instance_host=instance_host,
            instance_port=instance_port,
            ha_strategy=eureka_client.HA_STRATEGY_STICK
        )
        print(f"✅ Registered with Eureka as {app_name} at {eureka_url}")
    except Exception as e:
        print(f"⚠️ Could not register with Eureka: {e} (service will still work without Eureka)")


@app.route('/health', methods=['GET'])
def health():
    return jsonify({
        'status': 'UP',
        'service': 'equa-ai-service',
        'model_trained': model.is_trained,
        'model_version': model.model_version,
        'timestamp': datetime.now().isoformat()
    })


@app.route('/api/ai/predict', methods=['POST'])
def predict():
    """Predict default probability for a single customer."""
    data = request.get_json()
    if not data:
        return jsonify({'error': 'Request body is required'}), 400

    features = {
        'wallet_balance': data.get('wallet_balance', 0),
        'total_asset_value': data.get('total_asset_value', 0),
        'total_token_value': data.get('total_token_value', 0),
        'total_transactions': data.get('total_transactions', 0),
        'late_payments': data.get('late_payments', 0),
        'repayment_rate': data.get('repayment_rate', 0),
        'asset_coverage_ratio': data.get('asset_coverage_ratio', 0),
        'avg_transaction_amount': data.get('avg_transaction_amount', 0),
        'transaction_frequency': data.get('transaction_frequency', 0),
        'balance_volatility': data.get('balance_volatility', 0),
        'loyalty_points': data.get('loyalty_points', 0),
        'account_age_days': data.get('account_age_days', 0),
    }

    try:
        result = model.predict(features)
        return jsonify(result)
    except Exception as e:
        return jsonify({'error': str(e)}), 500


@app.route('/api/ai/predict/batch', methods=['POST'])
def batch_predict():
    """Predict default probability for multiple customers."""
    data = request.get_json()
    if not data or not isinstance(data, list):
        return jsonify({'error': 'Request body must be a JSON array'}), 400

    try:
        results = model.batch_predict(data)
        return jsonify({
            'predictions': results,
            'count': len(results),
            'timestamp': datetime.now().isoformat()
        })
    except Exception as e:
        return jsonify({'error': str(e)}), 500


@app.route('/api/ai/explain', methods=['POST'])
def explain():
    """Get detailed explanation of risk prediction."""
    data = request.get_json()
    if not data:
        return jsonify({'error': 'Request body is required'}), 400

    features = {
        'wallet_balance': data.get('wallet_balance', 0),
        'total_asset_value': data.get('total_asset_value', 0),
        'total_token_value': data.get('total_token_value', 0),
        'total_transactions': data.get('total_transactions', 0),
        'late_payments': data.get('late_payments', 0),
        'repayment_rate': data.get('repayment_rate', 0),
        'asset_coverage_ratio': data.get('asset_coverage_ratio', 0),
        'avg_transaction_amount': data.get('avg_transaction_amount', 0),
        'transaction_frequency': data.get('transaction_frequency', 0),
        'balance_volatility': data.get('balance_volatility', 0),
        'loyalty_points': data.get('loyalty_points', 0),
        'account_age_days': data.get('account_age_days', 0),
    }

    try:
        result = model.explain_prediction(features)
        return jsonify(result)
    except Exception as e:
        return jsonify({'error': str(e)}), 500


@app.route('/api/ai/train', methods=['POST'])
def train():
    """Retrain the model (with optional custom data)."""
    try:
        result = model.train()
        model.save_model()
        return jsonify({
            'message': 'Model retrained successfully',
            **result
        })
    except Exception as e:
        return jsonify({'error': str(e)}), 500


@app.route('/api/ai/model-info', methods=['GET'])
def model_info():
    """Get model information and feature importance."""
    if not model.is_trained:
        return jsonify({'error': 'Model not trained yet'}), 400

    rf_model = model.ensemble_model.named_estimators_['rf']
    feature_importance = dict(zip(
        model.feature_names,
        [round(float(x), 4) for x in rf_model.feature_importances_]
    ))

    return jsonify({
        'model_version': model.model_version,
        'is_trained': model.is_trained,
        'training_date': model.training_date,
        'algorithm': 'Ensemble (Random Forest + Gradient Boosting + Logistic Regression)',
        'voting': 'soft',
        'weights': {'random_forest': 3, 'gradient_boosting': 2, 'logistic_regression': 1},
        'feature_names': model.feature_names,
        'feature_importance': feature_importance,
        'credit_score_range': '300-850',
        'risk_levels': ['LOW', 'MEDIUM', 'HIGH', 'CRITICAL'],
        'endpoints': {
            '/api/ai/predict': 'POST - Single prediction',
            '/api/ai/predict/batch': 'POST - Batch predictions',
            '/api/ai/explain': 'POST - Prediction with explanation',
            '/api/ai/train': 'POST - Retrain model',
            '/api/ai/model-info': 'GET - Model information',
            '/health': 'GET - Health check'
        }
    })


@app.route('/api/ai/score', methods=['POST'])
def credit_score():
    """Calculate credit score only (lightweight endpoint)."""
    data = request.get_json()
    if not data:
        return jsonify({'error': 'Request body is required'}), 400

    features = {k: data.get(k, 0) for k in model.feature_names}

    try:
        result = model.predict(features)
        return jsonify({
            'credit_score': result['credit_score'],
            'risk_level': result['risk_level'],
            'default_probability': result['default_probability'],
            'recommendation': result['recommendation']
        })
    except Exception as e:
        return jsonify({'error': str(e)}), 500


# Register with Eureka in a background thread (non-blocking, 5s delay)
# Works both with Gunicorn and direct python execution
threading.Timer(5, register_with_eureka).start()

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=False)
