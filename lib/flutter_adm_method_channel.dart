import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'flutter_adm_platform_interface.dart';

/// An implementation of [FlutterAdmPlatform] that uses method channels.
class MethodChannelFlutterAdm extends FlutterAdmPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final _channel = const MethodChannel('flutter_adm');

  Function(String)? _onRegistrationCallback;
  Function(String)? _onMessageCallback;
  Function(String, bool)? _onSubscriptionCallback;

  @override
  void initialize() {

    _channel.setMethodCallHandler((call) async {
      if (call.method == 'onRegistrationId') {
        if(_onRegistrationCallback != null){
          _onRegistrationCallback!(call.arguments as String);
        }
      }

      if (call.method == 'onMessage') {
        if(_onMessageCallback != null){
          _onMessageCallback!(call.arguments as String);
        }
      }

      if (call.method == 'onSubscribe') {
        if(_onSubscriptionCallback != null){
          _onSubscriptionCallback!(call.arguments as String, true);
        }
      }

      if (call.method == 'onUnsubscribe') {
        if(_onSubscriptionCallback != null){
          _onSubscriptionCallback!(call.arguments as String, false);
        }
      }

    });

    _channel.invokeMethod('initialize');
  }

  @override
  void setOnSubscription(Function(String, bool) callback) {
    _onSubscriptionCallback = callback;
  }

  @override
  void setOnRegistrationId(Function(String) callback) {
    _onRegistrationCallback = callback;
  }

  @override
  void setOnMessage(Function(String) callback) {
    _onMessageCallback = callback;
  }

  @override
  void startRegister(){
    _channel.invokeMethod('startRegister');
  }

  @override
  Future<bool> isSupported() async {
    final bool isSupported = await _channel.invokeMethod('isSupported');
    return isSupported;
  }

  @override
  Future<void> setTopicSubscription(String topic, bool suscribe) async {
    await _channel.invokeMethod('setTopicSubscription', {'topic': topic, 'suscribe': suscribe});
  }
}
