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

  @override
  Future<void> initialize() {

    _channel.setMethodCallHandler((call) async {
      print('>>> onCall -> ' + call.method);

      if (call.method == 'onRegistrationId') {
        if(_onRegistrationCallback != null){
          _onRegistrationCallback(call.arguments as String);
        }
      }

      if (call.method == 'onMessage') {
        if(_onMessageCallback != null){
          _onMessageCallback(call.arguments as String);
        }
      }

    });

    return _channel.invokeMethod('initialize');
  }

  @override
  void setOnRegistrationId(Function(String) callback) {
    _onRegistrationCallback = callback;
  }

  @override
  void setOnMessage(Function(String) callback) {
    _onMessageCallback = callback;
  }
}
