import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:paymob_flutter_lib/helper.dart';

import 'models/payment.dart';
import 'models/payment_result.dart';
import 'paymob_flutter_lib_platform_interface.dart';

/// An implementation of [PaymobFlutterLibPlatform] that uses method channels.
class MethodChannelPaymobFlutterLib extends PaymobFlutterLibPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('paymob_flutter_lib');

  @override
  Future<String?> getPlatformVersion() async {
    final version =
        await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }

  @override
  Future<PaymentResult?> startPayActivityNoToken(
      Payment payment, CountrySubDomain country) async {
    String strPayment = paymentToJson(payment);
    final result = await methodChannel.invokeMethod<String>(
        'StartPayActivityNoToken',
        {"payment": strPayment, "countrySubDomain": country.subDomain});
    if (kDebugMode) {
      print("resulttttttttt");
      print(result);
    }
    // if (result == "Approved") {
    //   return PaymentResult(dataMessage: "Approved", maskedPan: "", token: "");
    // }
    return paymentResultFromJson(result!);
  }

  @override
  Future<String?> startPayActivityToken(
      Payment payment, CountrySubDomain country) async {
    final String result =
        await methodChannel.invokeMethod('StartPayActivityToken', {
      "payment": paymentToJson(payment),
      "countrySubDomain": country.subDomain
    });
    return result;
  }
}
