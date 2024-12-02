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
  Function()? _onNotificationClicked;


  @override
  void initialize() async {

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

      if (call.method == 'onNotificationClicked') {
        if(_onNotificationClicked != null){
          _onNotificationClicked!();
        }
        // try {
        //   final Map<String, dynamic> data = 
        //     json.decode(call.arguments as String) as Map<String, dynamic>;
        //   _notificationController?.add(data);
        // } catch (e) {
        //   print('Error parsing notification data: $e');
        // }
      }

    });

    try {
        await _channel.invokeMethod('initialize');
    } catch (e) {
        print('Error calling initialize: $e');
    }
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
  void startUnregister(){
    _channel.invokeMethod('startUnregister');
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
