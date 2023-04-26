package com.qodit.paymob_flutter_lib;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.core.JsonProcessingException;
//import com.paymob.acceptsdk.IntentConstants;
//import com.paymob.acceptsdk.PayActivity;
//import com.paymob.acceptsdk.PayActivityIntentKeys;
//import com.paymob.acceptsdk.PayResponseKeys;
//import com.paymob.acceptsdk.SaveCardResponseKeys;
// import com.paymob.acceptsdk.ThreeDSecureWebViewActivty;
import com.qodit.paymob_flutter_lib.acceptsdk.paymob.acceptsdk.IntentConstants;
import com.qodit.paymob_flutter_lib.acceptsdk.paymob.acceptsdk.PayActivity;
import com.qodit.paymob_flutter_lib.acceptsdk.paymob.acceptsdk.PayActivityIntentKeys;
import com.qodit.paymob_flutter_lib.acceptsdk.paymob.acceptsdk.PayResponseKeys;
import com.qodit.paymob_flutter_lib.acceptsdk.paymob.acceptsdk.SaveCardResponseKeys;
import com.qodit.paymob_flutter_lib.models.payment.Converter;
import com.qodit.paymob_flutter_lib.models.payment.Payment;
import com.qodit.paymob_flutter_lib.models.result.PaymentResult;
import com.qodit.paymob_flutter_lib.models.result.ResultConverter;
//import com.softworx.paymob_plugin.models.payment.Converter;
//import com.softworx.paymob_plugin.models.payment.Payment;
//import com.softworx.paymob_plugin.models.result.PaymentResult;
//import com.softworx.paymob_plugin.models.result.ResultConverter;

import java.io.IOException;
import java.util.Map;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.PluginRegistry;

/** PaymobFlutterLibPlugin */
public class PaymobFlutterLibPlugin implements FlutterPlugin, MethodCallHandler,ActivityAware, PluginRegistry.ActivityResultListener {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel channel;
  static final int ACCEPT_PAYMENT_REQUEST = 10;
  private static Activity activity;
  private static Context context;
  private static Result pendingResult;
  private static Payment payment;
  private static String countrySubDomain;

  public void StartPayActivityNoToken() {
    Intent pay_intent = new Intent(context, PayActivity.class);
    putNormalExtras(pay_intent);

    // this key is used to save the card by deafult.
    pay_intent.putExtra(PayActivityIntentKeys.SAVE_CARD_DEFAULT, payment.getSaveCardDefault());

    // this key is used to save the card by deafult.
    pay_intent.putExtra(PayActivityIntentKeys.COUNTRY_SUBDOMAIN,countrySubDomain);

    // this key is used to display the savecard checkbox.
    pay_intent.putExtra(PayActivityIntentKeys.SHOW_SAVE_CARD, payment.getShowSaveCard());

    //this key is used to set the theme color(Actionbar, statusBar, button).
    pay_intent.putExtra(PayActivityIntentKeys.THEME_COLOR, payment.getThemeColor());

    // this key is to wether display the Actionbar or not.
    pay_intent.putExtra("ActionBar",payment.getActionbar());

    // this key is used to define the language. takes for ex ("ar", "en") as inputs.
    pay_intent.putExtra("language",payment.getLanguage());

    activity.startActivityForResult(pay_intent, ACCEPT_PAYMENT_REQUEST);
  }

  public void StartPayActivityToken() {
    Intent pay_intent = new Intent(context, PayActivity.class);
    putNormalExtras(pay_intent);

    // this key is used to define the language. takes for ex ("ar", "en") as inputs.

    pay_intent.putExtra("language", payment.getLanguage());
    pay_intent.putExtra(PayActivityIntentKeys.TOKEN, payment.getToken());
    // card masked Pan in case of saved card.

    // this key is used to save the card by deafult.
    pay_intent.putExtra(PayActivityIntentKeys.COUNTRY_SUBDOMAIN,countrySubDomain);

    pay_intent.putExtra(PayActivityIntentKeys.MASKED_PAN_NUMBER, payment.getMaskedPanNumber());

    // this key is used to save the card by deafult.
    pay_intent.putExtra(PayActivityIntentKeys.SAVE_CARD_DEFAULT, payment.getSaveCardDefault());

    // this key is used to display the savecard checkbox.
    pay_intent.putExtra(PayActivityIntentKeys.SHOW_SAVE_CARD, payment.getShowSaveCard());

    pay_intent.putExtra("ActionBar",payment.getActionbar());
    //this key is used to set the theme color(Actionbar, statusBar, button).
    pay_intent.putExtra(PayActivityIntentKeys.THEME_COLOR, payment.getThemeColor());

    // this is the customer billing data, it should be passed, when paying with a saved Card.
    pay_intent.putExtra( PayActivityIntentKeys.FIRST_NAME, payment.getCustomer().getFirstName());
    pay_intent.putExtra(PayActivityIntentKeys.LAST_NAME, payment.getCustomer().getLastName());
    pay_intent.putExtra(PayActivityIntentKeys.BUILDING,payment.getCustomer().getBuilding());
    pay_intent.putExtra(PayActivityIntentKeys.FLOOR, payment.getCustomer().getFloor());
    pay_intent.putExtra(PayActivityIntentKeys.APARTMENT, payment.getCustomer().getApartment());
    pay_intent.putExtra(PayActivityIntentKeys.CITY, payment.getCustomer().getCity());
    pay_intent.putExtra(PayActivityIntentKeys.STATE, payment.getCustomer().getState());
    pay_intent.putExtra(PayActivityIntentKeys.COUNTRY,payment.getCustomer().getCountry());
    pay_intent.putExtra(PayActivityIntentKeys.EMAIL, payment.getCustomer().getEmail());
    pay_intent.putExtra(PayActivityIntentKeys.PHONE_NUMBER, payment.getCustomer().getPhoneNumber());
    pay_intent.putExtra(PayActivityIntentKeys.POSTAL_CODE,  payment.getCustomer().getPostalCode());

    activity.startActivityForResult(pay_intent, ACCEPT_PAYMENT_REQUEST);

  }

  private void putNormalExtras(Intent intent) {
    intent.putExtra(PayActivityIntentKeys.PAYMENT_KEY, payment.getPaymentKey());
    // intent.putExtra(PayActivityIntentKeys.THREE_D_SECURE_ACTIVITY_TITLE, "Verification");
  }

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "paymob_flutter_lib");
    channel.setMethodCallHandler(this);
    context = flutterPluginBinding.getApplicationContext();
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if (call.method.equals("getPlatformVersion")) {
      result.success("Android " + android.os.Build.VERSION.RELEASE);
    } else if (call.method.equals("StartPayActivityNoToken")) {
      try {
        pendingResult = result;
        final Map<String,Object> arg = call.arguments();

        countrySubDomain =  String.valueOf(arg.get("countrySubDomain"));

        payment = Converter.fromJsonString((String)arg.get("payment"));

        StartPayActivityNoToken();
      } catch (IOException e) {
        pendingResult.error("error", e.getMessage(), null);
      }
    } else if (call.method.equals("StartPayActivityToken")) {
      try {
        pendingResult = result;
        final Map<String,Object> arg = call.arguments();
        payment = Converter.fromJsonString((String)arg.get("payment"));

        countrySubDomain =  String.valueOf(arg.get("countrySubDomain"));

        StartPayActivityToken();

      } catch (IOException e) {
        pendingResult.error("error", e.getMessage(), null);
      }
    } else {
      result.notImplemented();
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }

  private void finishWithSuccess(String msg) {
    pendingResult.success(msg);
  }

  private void finishWithError(String errorCode, String errorMessage, String details) {
    pendingResult.error(errorCode, errorMessage, null);
  }

  public boolean onActivityResult(int requestCode, int resultCode, Intent data)  {
    Bundle extras = data.getExtras();

    if (requestCode == ACCEPT_PAYMENT_REQUEST) {

      if (resultCode == IntentConstants.USER_CANCELED) {
        // User canceled and did no payment request was fired
        finishWithError("USER_CANCELED", "User canceled!!!", null);
      }
      else if (resultCode == IntentConstants.MISSING_ARGUMENT) {
        // You forgot to pass an important key-value pair in the intent's extras
        finishWithError("MISSING_ARGUMENT", "Missing Argument == " + extras.getString(IntentConstants.MISSING_ARGUMENT_VALUE), null);
      }
      else if (resultCode == IntentConstants.TRANSACTION_ERROR) {
        // An error occurred while handling an API's response
        finishWithError("TRANSACTION_ERROR", "Reason == " + extras.getString(IntentConstants.TRANSACTION_ERROR_REASON), null);
      }
      else if (resultCode == IntentConstants.TRANSACTION_REJECTED) {
        // User attempted to pay but their transaction was rejected

        // Use the static keys declared in PayResponseKeys to extract the fields you want
        finishWithError("TRANSACTION_REJECTED", extras.getString(PayResponseKeys.DATA_MESSAGE), null);
      }
      else if (resultCode == IntentConstants.TRANSACTION_REJECTED_PARSING_ISSUE) {
        // User attempted to pay but their transaction was rejected. An error occured while reading the returned JSON
        finishWithError("TRANSACTION_REJECTED_PARSING_ISSUE",extras.getString(IntentConstants.RAW_PAY_RESPONSE), null );
      }
      else if (resultCode == IntentConstants.TRANSACTION_SUCCESSFUL) {
        // User finished their payment successfullyTRANSACTION_SUCCESSFUL

        // Use the static keys declared in PayResponseKeys to extract the fields you want
          // finishWithSuccess(extras.getString(PayResponseKeys.DATA_MESSAGE));
          PaymentResult paymentResult= new PaymentResult(); 
          paymentResult.setToken("");
          paymentResult.setMaskedPan("");
          paymentResult.setID(extras.getString(PayResponseKeys.ID));
          paymentResult.setDataMessage(extras.getString(PayResponseKeys.DATA_MESSAGE));
          paymentResult.setDataPayload(extras.getString(PayResponseKeys.payload));

        try {
          Log.d("payload",ResultConverter.toJsonString(paymentResult));
        } catch (JsonProcessingException e) {
          throw new RuntimeException(e);
        }
        // Use the static keys declared in PayResponseKeys to extract the fields you want

          // Use the static keys declared in PayResponseKeys to extract the fields you want
          // Use the static keys declared in SaveCardResponseKeys to extract the fields you want
          try {
            finishWithSuccess(ResultConverter.toJsonString(paymentResult));
          } catch (JsonProcessingException e) {
            e.printStackTrace();
          }

      }
      else if (resultCode == IntentConstants.TRANSACTION_SUCCESSFUL_PARSING_ISSUE) {
        // User finished their payment successfully. An error occured while reading the returned JSON.
        finishWithError("TRANSACTION_SUCCESSFUL_PARSING_ISSUE", "TRANSACTION_SUCCESSFUL - Parsing Issue", null);
      }
      else if (resultCode == IntentConstants.TRANSACTION_SUCCESSFUL_CARD_SAVED) {
        // User finished their payment successfully and card was TRANSACTION_SUCCESSFUL_CARD_SAVEDsaved.
        // ToastMaker.displayLongToast(this, "data " + extras.getString(PayResponseKeys.DATA_MESSAGE));
        // Log.d("token", "onActivityResult: "+extras.get(SaveCardResponseKeys.TOKEN));
        PaymentResult paymentResult= new PaymentResult(); 
        paymentResult.setToken(extras.getString(SaveCardResponseKeys.TOKEN));
        paymentResult.setMaskedPan(extras.getString(SaveCardResponseKeys.MASKED_PAN));
        paymentResult.setID(extras.getString(SaveCardResponseKeys.ID));
        // Use the static keys declared in PayResponseKeys to extract the fields you want

        // Use the static keys declared in PayResponseKeys to extract the fields you want
        // Use the static keys declared in SaveCardResponseKeys to extract the fields you want
        try {
          finishWithSuccess(ResultConverter.toJsonString(paymentResult));
        } catch (JsonProcessingException e) {
          e.printStackTrace();
        }
      } 
      else if (resultCode == IntentConstants.USER_CANCELED_3D_SECURE_VERIFICATION) {

        // Note that a payment process was attempted. You can extract the original returned values
        // Use the static keys declared in PayResponseKeys to extract the fields you want
        finishWithError("USER_CANCELED_3D_SECURE_VERIFICATION", "User canceled 3-d scure verification!!", extras.getString(PayResponseKeys.PENDING));
      }
      else if (resultCode == IntentConstants.USER_CANCELED_3D_SECURE_VERIFICATION_PARSING_ISSUE) {

        // Note that a payment process was attempted.
        // User finished their payment successfully. An error occured while reading the returned JSON.
        finishWithError("USER_CANCELED_3D_SECURE_VERIFICATION_PARSING_ISSUE", "User canceled 3-d scure verification - Parsing Issue!!", extras.getString(IntentConstants.RAW_PAY_RESPONSE));
      } 
      else {
        return false;
      }
      }
    return true;
    }


  @Override
  public void onAttachedToActivity(ActivityPluginBinding activityPluginBinding) {
    // TODO: your plugin is now attached to an Activity
         activity = activityPluginBinding.getActivity();
         activityPluginBinding.addActivityResultListener(this);
      }


  @Override
  public void onDetachedFromActivityForConfigChanges() {
    // TODO: the Activity your plugin was attached to was
    // destroyed to change configuration.
    // This call will be followed by onReattachedToActivityForConfigChanges().
  }

  @Override
  public void onReattachedToActivityForConfigChanges(ActivityPluginBinding activityPluginBinding) {
    // TODO: your plugin is now attached to a new Activity
    // after a configuration change.
  }

  @Override
  public void onDetachedFromActivity() {
    // TODO: your plugin is no longer associated with an Activity.
    // Clean up references.

  }
}
