package com.example.sipsupporterapp.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.sipsupporterapp.R;
import com.example.sipsupporterapp.database.SipSupporterDBHelper;
import com.example.sipsupporterapp.database.SipSupporterSchema;
import com.example.sipsupporterapp.model.AttachInfo;
import com.example.sipsupporterapp.model.AttachResult;
import com.example.sipsupporterapp.model.BankAccountResult;
import com.example.sipsupporterapp.model.CustomerPaymentInfo;
import com.example.sipsupporterapp.model.CustomerPaymentResult;
import com.example.sipsupporterapp.model.CustomerProductResult;
import com.example.sipsupporterapp.model.CustomerProducts;
import com.example.sipsupporterapp.model.CustomerResult;
import com.example.sipsupporterapp.model.CustomerSupportInfo;
import com.example.sipsupporterapp.model.CustomerSupportResult;
import com.example.sipsupporterapp.model.CustomerUserResult;
import com.example.sipsupporterapp.model.DateResult;
import com.example.sipsupporterapp.model.PaymentInfo;
import com.example.sipsupporterapp.model.PaymentResult;
import com.example.sipsupporterapp.model.PaymentSubjectResult;
import com.example.sipsupporterapp.model.ProductInfo;
import com.example.sipsupporterapp.model.ProductResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.model.SupportEventResult;
import com.example.sipsupporterapp.model.UserLoginParameter;
import com.example.sipsupporterapp.model.UserResult;
import com.example.sipsupporterapp.retrofit.AddProductResultDeserializer;
import com.example.sipsupporterapp.retrofit.AttachResultDeserializer;
import com.example.sipsupporterapp.retrofit.BankAccountResultDeserializer;
import com.example.sipsupporterapp.retrofit.CustomerPaymentResultDeserializer;
import com.example.sipsupporterapp.retrofit.CustomerProductResultDeserializer;
import com.example.sipsupporterapp.retrofit.CustomerResultDeserializer;
import com.example.sipsupporterapp.retrofit.CustomerSupportResultDeserializer;
import com.example.sipsupporterapp.retrofit.CustomerUserResultDeserializer;
import com.example.sipsupporterapp.retrofit.DateResultDeserializer;
import com.example.sipsupporterapp.retrofit.NewProductResultDeserializer;
import com.example.sipsupporterapp.retrofit.NoConnectivityException;
import com.example.sipsupporterapp.retrofit.PaymentResultDeserializer;
import com.example.sipsupporterapp.retrofit.PaymentSubjectResultDeserializer;
import com.example.sipsupporterapp.retrofit.ProductResultDeserializer;
import com.example.sipsupporterapp.retrofit.RetrofitInstance;
import com.example.sipsupporterapp.retrofit.SipSupporterService;
import com.example.sipsupporterapp.retrofit.SupportEventResultDeserializer;
import com.example.sipsupporterapp.retrofit.UserResultDeserializer;
import com.example.sipsupporterapp.viewmodel.SingleLiveEvent;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SipSupportRepository {

    public static SipSupportRepository instance;
    private SQLiteDatabase database;
    private Context context;
    private SipSupporterService sipSupporterService;

    private static final String TAG = SipSupportRepository.class.getSimpleName();

    private SingleLiveEvent<CustomerResult> customerResultSingleLiveEvent = new SingleLiveEvent<>();
    private MutableLiveData<String> wrongUserLoginKeyMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<String> notValueUserLoginKeyMutableLiveData = new MutableLiveData<>();
    private SingleLiveEvent<String> wrongIpAddressSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<UserResult> userResultSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<String> timeoutExceptionHappenSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> noConnectivityExceptionSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<String> errorUserResult = new SingleLiveEvent<>();
    private SingleLiveEvent<String> noConnection = new SingleLiveEvent<>();
    private SingleLiveEvent<UserResult> changedPassword = new SingleLiveEvent<>();
    private SingleLiveEvent<String> errorChangedPassword = new SingleLiveEvent<>();
    private SingleLiveEvent<CustomerSupportResult> customerSupportResult = new SingleLiveEvent<>();
    private SingleLiveEvent<String> errorCustomerSupportResult = new SingleLiveEvent<>();
    private SingleLiveEvent<CustomerUserResult> customerUserResultSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<String> errorCustomerUserResultSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<SupportEventResult> supportEventResultSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<String> errorSupportEventResultSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<CustomerSupportResult> customerSupportResultSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<String> errorCustomerSupportResultSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> dangerousUserSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<String> errorSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<DateResult> dateResultSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<CustomerProductResult> customerProductResultSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<String> errorCustomerProductResultSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<ProductResult> productResultSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<String> errorProductResultSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<ProductResult> getProductResultSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<String> getErrorProductResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<CustomerProductResult> PostCustomerProductsSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<String> errorPostCustomerProductsSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<ProductResult> productInfoSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<String> errorProductInfoSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<CustomerProductResult> deleteCustomerProductSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<String> errorDeleteCustomerProductSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<CustomerProductResult> editCustomerProductSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<String> errorEditCustomerProductSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<AttachResult> attachResultSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<String> errorAttachResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<CustomerPaymentResult> customerPaymentResultSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<String> errorCustomerPaymentResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<AttachResult> getAttachmentFilesViaCustomerPaymentIDSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<String> getErrorAttachmentFilesViaCustomerPaymentIDSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<AttachResult> getAttachmentFilesViaAttachIDSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<String> getErrorAttachmentFilesViaAttachIDSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<CustomerPaymentResult> addEditDeleteCustomerPaymentsSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<String> errorAddEditDeleteCustomerResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<BankAccountResult> bankAccountsResultSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<String> errorBankAccountsResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<CustomerPaymentResult> addCustomerPaymentsSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<String> errorAddCustomerPaymentSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<CustomerPaymentResult> deleteCustomerPaymentsSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<String> errorDeleteCustomerPaymentSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<CustomerPaymentResult> editCustomerPaymentsSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<String> errorEditCustomerPaymentSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<AttachResult> getAttachmentFilesViaCustomerProductIDSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<String> getErrorAttachmentFilesViaCustomerProductIDSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<AttachResult> getAttachmentFilesViaCustomerSupportIDSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<String> getErrorAttachmentFilesViaCustomerSupportIDSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<AttachResult> attachResultViaAttachIDSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<String> errorAttachResultViaAttachIDSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<PaymentResult> paymentResultPaymentsListAllBankAccountSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<String> errorPaymentResultPaymentsListAllBankAccountSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<PaymentResult> paymentResultPaymentsListByBankAccountSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<String> errorPaymentResultPaymentsListByBankAccountSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<PaymentResult> paymentResultPaymentsEditSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<String> errorPaymentResultPaymentsEditSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<PaymentResult> paymentResultPaymentsDeleteSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<String> errorPaymentResultPaymentsDeleteSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<PaymentResult> paymentResultPaymentsAddSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<String> errorPaymentResultPaymentsAddSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<PaymentSubjectResult> paymentSubjectResultPaymentSubjectsListSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<String> errorPaymentSubjectResultPaymentSubjectsListSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<AttachResult> deleteAttachResultSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<String> errorDeleteAttachResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<AttachResult> attachmentListResultByPaymentID = new SingleLiveEvent<>();
    private SingleLiveEvent<String> errorAttachmentListResultByPaymentID = new SingleLiveEvent<>();

    private SingleLiveEvent<PaymentSubjectResult> paymentSubjectInfoResultSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<String> errorPaymentSubjectInfoResultSingleLiveEvent = new SingleLiveEvent<>();

    private SipSupportRepository(Context context) {
        this.context = context.getApplicationContext();
        SipSupporterDBHelper helper = new SipSupporterDBHelper(context);
        database = helper.getWritableDatabase();
    }

    public static SipSupportRepository getInstance(Context context) {
        if (instance == null) {
            instance = new SipSupportRepository(context.getApplicationContext());
        }
        return instance;
    }

    public void getSipSupportServicePostUserLoginParameter(String baseUrl) {
        RetrofitInstance.getNewBaseUrl(baseUrl);
        sipSupporterService = RetrofitInstance
                .getRI(new TypeToken<UserResult>() {
                }.getType(), new UserResultDeserializer(), context).create(SipSupporterService.class);
    }

    public void getSupportServicePostCustomerParameter(String baseUrl) {
        RetrofitInstance.getNewBaseUrl(baseUrl);
        sipSupporterService = RetrofitInstance
                .getRI(new TypeToken<CustomerResult>() {
                }.getType(), new CustomerResultDeserializer(), context).create(SipSupporterService.class);
    }

    public void getSipSupportServiceChangePassword(String baseUrl) {
        RetrofitInstance.getNewBaseUrl(baseUrl);
        sipSupporterService = RetrofitInstance
                .getRI(new TypeToken<UserResult>() {
                }.getType(), new UserResultDeserializer(), context).create(SipSupporterService.class);
    }

    public void getSipSupportServiceCustomerSupportResult(String baseUrl) {
        RetrofitInstance.getNewBaseUrl(baseUrl);
        sipSupporterService = RetrofitInstance
                .getRI(new TypeToken<CustomerSupportResult>() {
                }.getType(), new CustomerSupportResultDeserializer(), context).create(SipSupporterService.class);
    }

    public void getSipSupportServiceCustomerUserResult(String baseUrl) {
        RetrofitInstance.getNewBaseUrl(baseUrl);
        sipSupporterService = RetrofitInstance
                .getRI(new TypeToken<CustomerUserResult>() {
                }.getType(), new CustomerUserResultDeserializer(), context).create(SipSupporterService.class);
    }

    public void getSipSupportServiceSupportEventResult(String baseUrl) {
        RetrofitInstance.getNewBaseUrl(baseUrl);
        sipSupporterService = RetrofitInstance
                .getRI(new TypeToken<SupportEventResult>() {
                }.getType(), new SupportEventResultDeserializer(), context).create(SipSupporterService.class);
    }

    public void getSipSupportServicePostCustomerSupportResult(String baseUrl) {
        RetrofitInstance.getNewBaseUrl(baseUrl);
        sipSupporterService = RetrofitInstance
                .getRI(new TypeToken<CustomerSupportResult>() {
                }.getType(), new CustomerSupportResultDeserializer(), context).create(SipSupporterService.class);
    }

    public void getSipSupportServiceGetDateResult(String baseUrl) {
        RetrofitInstance.getNewBaseUrl(baseUrl);
        sipSupporterService = RetrofitInstance
                .getRI(new TypeToken<DateResult>() {
                }.getType(), new DateResultDeserializer(), context).create(SipSupporterService.class);
    }

    public void getSipSupportServiceGetCustomerProductResult(String baseUrl) {
        RetrofitInstance.getNewBaseUrl(baseUrl);
        sipSupporterService = RetrofitInstance
                .getRI(new TypeToken<CustomerProductResult>() {
                }.getType(), new ProductResultDeserializer(), context).create(SipSupporterService.class);
    }

    public void getSipSupportServiceGetProductResult(String baseUrl) {
        RetrofitInstance.getNewBaseUrl(baseUrl);
        sipSupporterService = RetrofitInstance
                .getRI(new TypeToken<ProductResult>() {
                }.getType(), new AddProductResultDeserializer(), context).create(SipSupporterService.class);
    }

    public void getSipSupportServicePostCustomerProducts(String baseUrl) {
        RetrofitInstance.getNewBaseUrl(baseUrl);
        sipSupporterService = RetrofitInstance
                .getRI(new TypeToken<CustomerProductResult>() {
                }.getType(), new CustomerProductResultDeserializer(), context).create(SipSupporterService.class);
    }

    public void getSipSupportServiceForGetProductInfo(String baseUrl) {
        RetrofitInstance.getNewBaseUrl(baseUrl);
        sipSupporterService = RetrofitInstance
                .getRI(new TypeToken<ProductResult>() {
                }.getType(), new NewProductResultDeserializer(), context).create(SipSupporterService.class);
    }

    public void getSipSupportServiceForDeleteCustomerProduct(String baseUrl) {
        RetrofitInstance.getNewBaseUrl(baseUrl);
        sipSupporterService = RetrofitInstance
                .getRI(new TypeToken<CustomerProductResult>() {
                }.getType(), new CustomerProductResultDeserializer(), context).create(SipSupporterService.class);
    }

    public void getSipSupportServiceForEditCustomerProduct(String baseUrl) {
        RetrofitInstance.getNewBaseUrl(baseUrl);
        sipSupporterService = RetrofitInstance
                .getRI(new TypeToken<CustomerProductResult>() {
                }.getType(), new CustomerProductResultDeserializer(), context).create(SipSupporterService.class);
    }

    public void getSipSupportServiceAttach(String baseUrl) {
        RetrofitInstance.getNewBaseUrl(baseUrl);
        sipSupporterService = RetrofitInstance
                .getRI(new TypeToken<AttachResult>() {
                }.getType(), new AttachResultDeserializer(), context).create(SipSupporterService.class);
    }

    public void getSipSupportServiceCustomerPaymentResult(String baseUrl) {
        RetrofitInstance.getNewBaseUrl(baseUrl);
        sipSupporterService = RetrofitInstance
                .getRI(new TypeToken<CustomerPaymentResult>() {
                }.getType(), new CustomerPaymentResultDeserializer(), context).create(SipSupporterService.class);
    }

    public void getSipSupportServiceGetAttachmentFilesViaCustomerPaymentID(String baseUrl) {
        RetrofitInstance.getNewBaseUrl(baseUrl);
        sipSupporterService = RetrofitInstance
                .getRI(new TypeToken<AttachResult>() {
                }.getType(), new AttachResultDeserializer(), context).create(SipSupporterService.class);
    }

    public void getSipSupportServiceGetAttachmentFileViaAttachIDRetrofitInstance(String baseUrl) {
        RetrofitInstance.getNewBaseUrl(baseUrl);
        sipSupporterService = RetrofitInstance
                .getRI(new TypeToken<AttachResult>() {
                }.getType(), new AttachResultDeserializer(), context).create(SipSupporterService.class);
    }

    public void getSipSupportServiceAddCustomerPayments(String baseUrl) {
        RetrofitInstance.getNewBaseUrl(baseUrl);
        sipSupporterService = RetrofitInstance
                .getRI(new TypeToken<CustomerPaymentResult>() {
                }.getType(), new CustomerPaymentResultDeserializer(), context).create(SipSupporterService.class);
    }

    public void getSipSupportServiceEditCustomerPayments(String baseUrl) {
        RetrofitInstance.getNewBaseUrl(baseUrl);
        sipSupporterService = RetrofitInstance
                .getRI(new TypeToken<CustomerPaymentResult>() {
                }.getType(), new CustomerPaymentResultDeserializer(), context).create(SipSupporterService.class);
    }

    public void getSipSupportServiceDeleteCustomerPayments(String baseUrl) {
        RetrofitInstance.getNewBaseUrl(baseUrl);
        sipSupporterService = RetrofitInstance
                .getRI(new TypeToken<CustomerPaymentResult>() {
                }.getType(), new CustomerPaymentResultDeserializer(), context).create(SipSupporterService.class);
    }

    public void getSipSupportServiceGetBankAccountResult(String baseUrl) {
        RetrofitInstance.getNewBaseUrl(baseUrl);
        sipSupporterService = RetrofitInstance
                .getRI(new TypeToken<CustomerPaymentResult>() {
                }.getType(), new BankAccountResultDeserializer(), context).create(SipSupporterService.class);
    }

    public void getSipSupportServiceGetAttachmentFilesViaCustomerProductID(String baseUrl) {
        RetrofitInstance.getNewBaseUrl(baseUrl);
        sipSupporterService = RetrofitInstance
                .getRI(new TypeToken<AttachResult>() {
                }.getType(), new AttachResultDeserializer(), context).create(SipSupporterService.class);
    }

    public void getSipSupportServiceGetAttachmentFilesViaCustomerSupportID(String baseUrl) {
        RetrofitInstance.getNewBaseUrl(baseUrl);
        sipSupporterService = RetrofitInstance
                .getRI(new TypeToken<AttachResult>() {
                }.getType(), new AttachResultDeserializer(), context).create(SipSupporterService.class);
    }

    public void getSipSupportServicePaymentsListByBankAccount(String baseUrl) {
        RetrofitInstance.getNewBaseUrl(baseUrl);
        sipSupporterService = RetrofitInstance
                .getRI(new TypeToken<PaymentResult>() {
                }.getType(), new PaymentResultDeserializer(), context).create(SipSupporterService.class);
    }

    public void getSipSupportServicePaymentsEdit(String baseUrl) {
        RetrofitInstance.getNewBaseUrl(baseUrl);
        sipSupporterService = RetrofitInstance
                .getRI(new TypeToken<PaymentResult>() {
                }.getType(), new PaymentResultDeserializer(), context).create(SipSupporterService.class);
    }

    public void getSipSupportServicePaymentsDelete(String baseUrl) {
        RetrofitInstance.getNewBaseUrl(baseUrl);
        sipSupporterService = RetrofitInstance
                .getRI(new TypeToken<PaymentResult>() {
                }.getType(), new PaymentResultDeserializer(), context).create(SipSupporterService.class);
    }

    public void getSipSupportServicePaymentsAdd(String baseUrl) {
        RetrofitInstance.getNewBaseUrl(baseUrl);
        sipSupporterService = RetrofitInstance
                .getRI(new TypeToken<PaymentResult>() {
                }.getType(), new PaymentResultDeserializer(), context).create(SipSupporterService.class);
    }

    public void getSipSupporterServicePaymentSubjects(String baseUrl) {
        RetrofitInstance.getNewBaseUrl(baseUrl);
        sipSupporterService = RetrofitInstance
                .getRI(new TypeToken<PaymentSubjectResult>() {
                }.getType(), new PaymentSubjectResultDeserializer(), context).create(SipSupporterService.class);
    }

    public void getSipSupportServiceDeleteAttach(String baseUrl) {
        RetrofitInstance.getNewBaseUrl(baseUrl);
        sipSupporterService = RetrofitInstance
                .getRI(new TypeToken<AttachResult>() {
                }.getType(), new AttachResultDeserializer(), context).create(SipSupporterService.class);
    }

    public void getSipSupportServiceGetAttachmentListByPaymentID(String baseUrl) {
        RetrofitInstance.getNewBaseUrl(baseUrl);
        sipSupporterService = RetrofitInstance
                .getRI(new TypeToken<AttachResult>() {
                }.getType(), new AttachResultDeserializer(), context).create(SipSupporterService.class);
    }

    public void getSipSupporterServicePaymentInfo(String baseUrl) {
        RetrofitInstance.getNewBaseUrl(baseUrl);
        sipSupporterService = RetrofitInstance
                .getRI(new TypeToken<PaymentSubjectResult>() {
                }.getType(), new PaymentSubjectResultDeserializer(), context).create(SipSupporterService.class);
    }

    public SingleLiveEvent<CustomerResult> getCustomerResultSingleLiveEvent() {
        return customerResultSingleLiveEvent;
    }

    public MutableLiveData<String> getWrongUserLoginKeyMutableLiveData() {
        return wrongUserLoginKeyMutableLiveData;
    }

    public MutableLiveData<String> getNotValueUserLoginKeyMutableLiveData() {
        return notValueUserLoginKeyMutableLiveData;
    }

    public SingleLiveEvent<String> getWrongIpAddressSingleLiveEvent() {
        return wrongIpAddressSingleLiveEvent;
    }

    public SingleLiveEvent<UserResult> getUserResultSingleLiveEvent() {
        return userResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getTimeoutExceptionHappenSingleLiveEvent() {
        return timeoutExceptionHappenSingleLiveEvent;
    }

    public SingleLiveEvent<Boolean> getNoConnectivityExceptionSingleLiveEvent() {
        return noConnectivityExceptionSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorUserResult() {
        return errorUserResult;
    }

    public SingleLiveEvent<String> getNoConnection() {
        return noConnection;
    }

    public SingleLiveEvent<UserResult> getChangedPassword() {
        return changedPassword;
    }

    public SingleLiveEvent<String> getErrorChangedPassword() {
        return errorChangedPassword;
    }

    public SingleLiveEvent<CustomerSupportResult> getCustomerSupportResult() {
        return customerSupportResult;
    }

    public SingleLiveEvent<String> getErrorCustomerSupportResult() {
        return errorCustomerSupportResult;
    }

    public SingleLiveEvent<CustomerUserResult> getCustomerUserResultSingleLiveEvent() {
        return customerUserResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorCustomerUserResultSingleLiveEvent() {
        return errorCustomerUserResultSingleLiveEvent;
    }

    public SingleLiveEvent<SupportEventResult> getSupportEventResultSingleLiveEvent() {
        return supportEventResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorSupportEventResultSingleLiveEvent() {
        return errorSupportEventResultSingleLiveEvent;
    }

    public SingleLiveEvent<CustomerSupportResult> getCustomerSupportResultSingleLiveEvent() {
        return customerSupportResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorCustomerSupportResultSingleLiveEvent() {
        return errorCustomerSupportResultSingleLiveEvent;
    }

    public SingleLiveEvent<Boolean> getDangerousUserSingleLiveEvent() {
        return dangerousUserSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorSingleLiveEvent() {
        return errorSingleLiveEvent;
    }

    public SingleLiveEvent<DateResult> getDateResultSingleLiveEvent() {
        return dateResultSingleLiveEvent;
    }

    public SingleLiveEvent<CustomerProductResult> getCustomerProductResultSingleLiveEvent() {
        return customerProductResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorCustomerProductResultSingleLiveEvent() {
        return errorCustomerProductResultSingleLiveEvent;
    }

    public SingleLiveEvent<ProductResult> getProductResultSingleLiveEvent() {
        return productResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorProductResultSingleLiveEvent() {
        return errorProductResultSingleLiveEvent;
    }

    public SingleLiveEvent<ProductResult> getGetProductResultSingleLiveEvent() {
        return getProductResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getGetErrorProductResultSingleLiveEvent() {
        return getErrorProductResultSingleLiveEvent;
    }

    public SingleLiveEvent<CustomerProductResult> getPostCustomerProductsSingleLiveEvent() {
        return PostCustomerProductsSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorPostCustomerProductsSingleLiveEvent() {
        return errorPostCustomerProductsSingleLiveEvent;
    }

    public SingleLiveEvent<ProductResult> getProductInfoSingleLiveEvent() {
        return productInfoSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorProductInfoSingleLiveEvent() {
        return errorProductInfoSingleLiveEvent;
    }

    public SingleLiveEvent<CustomerProductResult> getDeleteCustomerProductSingleLiveEvent() {
        return deleteCustomerProductSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorDeleteCustomerProductSingleLiveEvent() {
        return errorDeleteCustomerProductSingleLiveEvent;
    }

    public SingleLiveEvent<CustomerProductResult> getEditCustomerProductSingleLiveEvent() {
        return editCustomerProductSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorEditCustomerProductSingleLiveEvent() {
        return errorEditCustomerProductSingleLiveEvent;
    }

    public SingleLiveEvent<AttachResult> getAttachResultSingleLiveEvent() {
        return attachResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorAttachResultSingleLiveEvent() {
        return errorAttachResultSingleLiveEvent;
    }

    public SingleLiveEvent<CustomerPaymentResult> getCustomerPaymentResultSingleLiveEvent() {
        return customerPaymentResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorCustomerPaymentResultSingleLiveEvent() {
        return errorCustomerPaymentResultSingleLiveEvent;
    }

    public SingleLiveEvent<AttachResult> getGetAttachmentFilesViaCustomerPaymentIDSingleLiveEvent() {
        return getAttachmentFilesViaCustomerPaymentIDSingleLiveEvent;
    }

    public SingleLiveEvent<String> getGetErrorAttachmentFilesViaCustomerPaymentIDSingleLiveEvent() {
        return getErrorAttachmentFilesViaCustomerPaymentIDSingleLiveEvent;
    }

    public SingleLiveEvent<AttachResult> getGetAttachmentFilesViaAttachIDSingleLiveEvent() {
        return getAttachmentFilesViaAttachIDSingleLiveEvent;
    }

    public SingleLiveEvent<String> getGetErrorAttachmentFilesViaAttachIDSingleLiveEvent() {
        return getErrorAttachmentFilesViaAttachIDSingleLiveEvent;
    }

    public SingleLiveEvent<CustomerPaymentResult> getAddEditDeleteCustomerPaymentsSingleLiveEvent() {
        return addEditDeleteCustomerPaymentsSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorAddEditDeleteCustomerResultSingleLiveEvent() {
        return errorAddEditDeleteCustomerResultSingleLiveEvent;
    }

    public SingleLiveEvent<BankAccountResult> getBankAccountsResultSingleLiveEvent() {
        return bankAccountsResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorBankAccountsResultSingleLiveEvent() {
        return errorBankAccountsResultSingleLiveEvent;
    }

    public SingleLiveEvent<CustomerPaymentResult> getAddCustomerPaymentsSingleLiveEvent() {
        return addCustomerPaymentsSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorAddCustomerPaymentSingleLiveEvent() {
        return errorAddCustomerPaymentSingleLiveEvent;
    }

    public SingleLiveEvent<CustomerPaymentResult> getDeleteCustomerPaymentsSingleLiveEvent() {
        return deleteCustomerPaymentsSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorDeleteCustomerPaymentSingleLiveEvent() {
        return errorDeleteCustomerPaymentSingleLiveEvent;
    }

    public SingleLiveEvent<CustomerPaymentResult> getEditCustomerPaymentsSingleLiveEvent() {
        return editCustomerPaymentsSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorEditCustomerPaymentSingleLiveEvent() {
        return errorEditCustomerPaymentSingleLiveEvent;
    }

    public SingleLiveEvent<AttachResult> getGetAttachmentFilesViaCustomerProductIDSingleLiveEvent() {
        return getAttachmentFilesViaCustomerProductIDSingleLiveEvent;
    }

    public SingleLiveEvent<String> getGetErrorAttachmentFilesViaCustomerProductIDSingleLiveEvent() {
        return getErrorAttachmentFilesViaCustomerProductIDSingleLiveEvent;
    }

    public SingleLiveEvent<AttachResult> getGetAttachmentFilesViaCustomerSupportIDSingleLiveEvent() {
        return getAttachmentFilesViaCustomerSupportIDSingleLiveEvent;
    }

    public SingleLiveEvent<String> getGetErrorAttachmentFilesViaCustomerSupportIDSingleLiveEvent() {
        return getErrorAttachmentFilesViaCustomerSupportIDSingleLiveEvent;
    }

    public SingleLiveEvent<AttachResult> getAttachResultViaAttachIDSingleLiveEvent() {
        return attachResultViaAttachIDSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorAttachResultViaAttachIDSingleLiveEvent() {
        return errorAttachResultViaAttachIDSingleLiveEvent;
    }

    public SingleLiveEvent<PaymentResult> getPaymentResultPaymentsListAllBankAccountSingleLiveEvent() {
        return paymentResultPaymentsListAllBankAccountSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorPaymentResultPaymentsListAllBankAccountSingleLiveEvent() {
        return errorPaymentResultPaymentsListAllBankAccountSingleLiveEvent;
    }

    public SingleLiveEvent<PaymentResult> getPaymentResultPaymentsListByBankAccountSingleLiveEvent() {
        return paymentResultPaymentsListByBankAccountSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorPaymentResultPaymentsListByBankAccountSingleLiveEvent() {
        return errorPaymentResultPaymentsListByBankAccountSingleLiveEvent;
    }

    public SingleLiveEvent<PaymentResult> getPaymentResultPaymentsEditSingleLiveEvent() {
        return paymentResultPaymentsEditSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorPaymentResultPaymentsEditSingleLiveEvent() {
        return errorPaymentResultPaymentsEditSingleLiveEvent;
    }

    public SingleLiveEvent<PaymentResult> getPaymentResultPaymentsDeleteSingleLiveEvent() {
        return paymentResultPaymentsDeleteSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorPaymentResultPaymentsDeleteSingleLiveEvent() {
        return errorPaymentResultPaymentsDeleteSingleLiveEvent;
    }

    public SingleLiveEvent<PaymentSubjectResult> getPaymentSubjectsResultSingleLiveEvent() {
        return paymentSubjectResultPaymentSubjectsListSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorPaymentSubjectsResultSingleLiveEvent() {
        return errorPaymentSubjectResultPaymentSubjectsListSingleLiveEvent;
    }

    public SingleLiveEvent<PaymentResult> getPaymentResultPaymentsAddSingleLiveEvent() {
        return paymentResultPaymentsAddSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorPaymentResultPaymentsAddSingleLiveEvent() {
        return errorPaymentResultPaymentsAddSingleLiveEvent;
    }

    public SingleLiveEvent<AttachResult> getDeleteAttachResultSingleLiveEvent() {
        return deleteAttachResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorDeleteAttachResultSingleLiveEvent() {
        return errorDeleteAttachResultSingleLiveEvent;
    }

    public SingleLiveEvent<AttachResult> getAttachmentListResultByPaymentID() {
        return attachmentListResultByPaymentID;
    }

    public SingleLiveEvent<String> getErrorAttachmentListResultByPaymentID() {
        return errorAttachmentListResultByPaymentID;
    }

    public SingleLiveEvent<PaymentSubjectResult> getPaymentSubjectInfoResultSingleLiveEvent() {
        return paymentSubjectInfoResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorPaymentSubjectInfoResultSingleLiveEvent() {
        return errorPaymentSubjectInfoResultSingleLiveEvent;
    }

    public void insertServerData(ServerData serverData) {
        ContentValues values = new ContentValues();

        values.put(SipSupporterSchema.ServerDataTable.Cols.CENTER_NAME, serverData.getCenterName());
        values.put(SipSupporterSchema.ServerDataTable.Cols.IP_ADDRESS, serverData.getIpAddress());
        values.put(SipSupporterSchema.ServerDataTable.Cols.PORT, serverData.getPort());

        database.insert(SipSupporterSchema.ServerDataTable.NAME, null, values);
    }

    public List<ServerData> getServerDataList() {
        List<ServerData> serverDataList = new ArrayList<>();
        Cursor cursor = database.query(
                SipSupporterSchema.ServerDataTable.NAME,
                null,
                null,
                null,
                null,
                null,
                null);

        if (cursor == null || cursor.getCount() == 0) {
            return serverDataList;
        }

        try {

            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {

                String centerName = cursor.getString(cursor.getColumnIndex(SipSupporterSchema.ServerDataTable.Cols.CENTER_NAME));
                String ipAddress = cursor.getString(cursor.getColumnIndex(SipSupporterSchema.ServerDataTable.Cols.IP_ADDRESS));
                String port = cursor.getString(cursor.getColumnIndex(SipSupporterSchema.ServerDataTable.Cols.PORT));

                ServerData serverData = new ServerData(centerName, ipAddress, port);

                serverDataList.add(serverData);

                cursor.moveToNext();
            }

        } finally {

            cursor.close();
        }
        return serverDataList;
    }

    public ServerData getServerData(String centerName) {
        ServerData serverData = new ServerData();
        String selection = "centerName=?";
        String[] selectionArgs = {centerName};
        Cursor cursor = database.query(
                SipSupporterSchema.ServerDataTable.NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null);

        if (cursor == null || cursor.getCount() == 0) {
            return serverData;
        }

        try {

            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {

                String ipAddress = cursor.getString(cursor.getColumnIndex(SipSupporterSchema.ServerDataTable.Cols.IP_ADDRESS));
                String port = cursor.getString(cursor.getColumnIndex(SipSupporterSchema.ServerDataTable.Cols.PORT));

                serverData.setCenterName(centerName);
                serverData.setIpAddress(ipAddress);
                serverData.setPort(port);

                cursor.moveToNext();
            }

        } finally {

            cursor.close();
        }
        return serverData;
    }

    public void deleteServerData(ServerData serverData) {
        String whereClause = "centerName=?";
        String whereArgs[] = {serverData.getCenterName()};
        database.delete(SipSupporterSchema.ServerDataTable.NAME, whereClause, whereArgs);
    }

    public void fetchUserResult(String path, UserLoginParameter userLoginParameter) {
        sipSupporterService.postUserLoginParameter(path, userLoginParameter)
                .enqueue(new Callback<UserResult>() {
                    @Override
                    public void onResponse(Call<UserResult> call, Response<UserResult> response) {
                        if (response.isSuccessful()) {
                            userResultSingleLiveEvent.setValue(response.body());
                        } else {
                            try {
                                Gson gson = new Gson();
                                UserResult userResult = gson.fromJson(response.errorBody().string(), UserResult.class);
                                if (Integer.valueOf(userResult.getErrorCode()) <= -9001) {
                                    dangerousUserSingleLiveEvent.setValue(true);
                                } else {
                                    errorUserResult.setValue(userResult.getError());
                                }
                            } catch (IOException e) {
                                Log.e(TAG, e.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<UserResult> call, Throwable t) {
                        if (t instanceof NoConnectivityException) {
                            noConnection.setValue(t.getMessage());
                        } else if (t instanceof SocketTimeoutException) {
                            timeoutExceptionHappenSingleLiveEvent.setValue(context.getResources().getString(R.string.timeout_exception_happen_message));
                        } else {
                            wrongIpAddressSingleLiveEvent.setValue(context.getResources().getString(R.string.not_exist_server));
                        }
                    }
                });
    }

    public void fetchCustomerResult(String path, String userLoginKey, String customerName) {
        sipSupporterService.getCustomers(path, userLoginKey, customerName).enqueue(new Callback<CustomerResult>() {
            @Override
            public void onResponse(Call<CustomerResult> call, Response<CustomerResult> response) {
                if (response.isSuccessful()) {
                    customerResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        CustomerResult customerResult = gson.fromJson(response.errorBody().string(), CustomerResult.class);
                        if (Integer.valueOf(customerResult.getErrorCode()) <= -9001) {
                            dangerousUserSingleLiveEvent.setValue(true);
                        } else {
                            errorSingleLiveEvent.setValue(customerResult.getError());
                        }
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<CustomerResult> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    noConnection.setValue(t.getMessage());
                } else if (t instanceof SocketTimeoutException) {
                    timeoutExceptionHappenSingleLiveEvent.setValue(context.getResources().getString(R.string.timeout_exception_happen_message));
                } else {
                    Log.e(TAG, t.getMessage(), t);
                }
            }
        });
    }

    public void changePassword(String path, String userLoginKey, String newPassword) {
        sipSupporterService.changePassword(path, userLoginKey, newPassword).enqueue(new Callback<UserResult>() {
            @Override
            public void onResponse(Call<UserResult> call, Response<UserResult> response) {
                if (response.isSuccessful()) {
                    changedPassword.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        UserResult userResult = gson.fromJson(response.errorBody().string(), UserResult.class);
                        if (Integer.valueOf(userResult.getErrorCode()) <= -9001) {
                            dangerousUserSingleLiveEvent.setValue(true);
                        } else {
                            errorChangedPassword.setValue(userResult.getError());
                        }
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<UserResult> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    noConnection.setValue(t.getMessage());
                } else if (t instanceof SocketTimeoutException) {
                    timeoutExceptionHappenSingleLiveEvent.setValue(context.getResources().getString(R.string.timeout_exception_happen_message));
                } else {
                    Log.e(TAG, t.getMessage(), t);
                }
            }
        });
    }

    public void getCustomerSupportResult(String path, String userLoginKey, int customerID) {
        sipSupporterService.getCustomerSupportResult(path, userLoginKey, customerID).enqueue(new Callback<CustomerSupportResult>() {
            @Override
            public void onResponse(Call<CustomerSupportResult> call, Response<CustomerSupportResult> response) {
                if (response.isSuccessful()) {
                    customerSupportResult.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        CustomerSupportResult customerSupportResult = gson.fromJson(response.errorBody().string(), CustomerSupportResult.class);
                        if (Integer.valueOf(customerSupportResult.getErrorCode()) <= -9001) {
                            dangerousUserSingleLiveEvent.setValue(true);
                        } else {
                            errorCustomerSupportResult.setValue(customerSupportResult.getError());
                        }
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<CustomerSupportResult> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    noConnection.setValue(t.getMessage());
                } else if (t instanceof SocketTimeoutException) {
                    timeoutExceptionHappenSingleLiveEvent.setValue(context.getResources().getString(R.string.timeout_exception_happen_message));
                } else {
                    Log.e(TAG, t.getMessage(), t);
                }
            }
        });
    }


    public void fetchCustomerUserResult(String path, String userLoginKey, int customerID) {
        sipSupporterService.getCustomerUserResult(path, userLoginKey, customerID).enqueue(new Callback<CustomerUserResult>() {
            @Override
            public void onResponse(Call<CustomerUserResult> call, Response<CustomerUserResult> response) {
                if (response.isSuccessful()) {
                    customerUserResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        CustomerUserResult customerUserResult = gson.fromJson(response.errorBody().string(), CustomerUserResult.class);
                        if (Integer.valueOf(customerUserResult.getErrorCode()) <= -9001) {
                            dangerousUserSingleLiveEvent.setValue(true);
                        } else {
                            errorCustomerUserResultSingleLiveEvent.setValue(customerUserResult.getError());
                        }
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<CustomerUserResult> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    noConnection.setValue(t.getMessage());
                } else if (t instanceof SocketTimeoutException) {
                    timeoutExceptionHappenSingleLiveEvent.setValue(context.getResources().getString(R.string.timeout_exception_happen_message));
                } else {
                    Log.e(TAG, t.getMessage(), t);
                }
            }
        });
    }


    public void fetchSupportEventResult(String path, String userLoginKey) {
        sipSupporterService.getSupportEventResult(path, userLoginKey).enqueue(new Callback<SupportEventResult>() {
            @Override
            public void onResponse(Call<SupportEventResult> call, Response<SupportEventResult> response) {
                if (response.isSuccessful()) {
                    supportEventResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        SupportEventResult supportEventResult = gson.fromJson(response.errorBody().string(), SupportEventResult.class);
                        if (Integer.valueOf(supportEventResult.getErrorCode()) <= -9001) {
                            dangerousUserSingleLiveEvent.setValue(true);
                        } else {
                            errorSupportEventResultSingleLiveEvent.setValue(supportEventResult.getError());
                        }
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<SupportEventResult> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    noConnection.setValue(t.getMessage());
                } else if (t instanceof SocketTimeoutException) {
                    timeoutExceptionHappenSingleLiveEvent.setValue(context.getResources().getString(R.string.timeout_exception_happen_message));
                } else {
                    Log.e(TAG, t.getMessage(), t);
                }
            }
        });
    }

    public void postCustomerSupportInfo(String path, String userLoginKey, CustomerSupportInfo customerSupportInfo) {
        sipSupporterService.postCustomerSupportInfo(path, userLoginKey, customerSupportInfo).enqueue(new Callback<CustomerSupportResult>() {
            @Override
            public void onResponse(Call<CustomerSupportResult> call, Response<CustomerSupportResult> response) {
                if (response.isSuccessful()) {
                    customerSupportResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        CustomerSupportResult customerSupportResult = gson.fromJson(response.errorBody().string(), CustomerSupportResult.class);
                        if (Integer.valueOf(customerSupportResult.getErrorCode()) <= -9001) {
                            dangerousUserSingleLiveEvent.setValue(true);
                        } else {
                            errorCustomerSupportResultSingleLiveEvent.setValue(customerSupportResult.getError());
                        }
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<CustomerSupportResult> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    noConnection.setValue(t.getMessage());
                } else if (t instanceof SocketTimeoutException) {
                    timeoutExceptionHappenSingleLiveEvent.setValue(context.getResources().getString(R.string.timeout_exception_happen_message));
                } else {
                    Log.e(TAG, t.getMessage(), t);
                }
            }
        });
    }

    public void fetchDateResult(String path, String userLoginKey) {
        sipSupporterService.getDateResult(path, userLoginKey).enqueue(new Callback<DateResult>() {
            @Override
            public void onResponse(Call<DateResult> call, Response<DateResult> response) {
                if (response.isSuccessful()) {
                    dateResultSingleLiveEvent.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<DateResult> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    noConnection.setValue(t.getMessage());
                } else if (t instanceof SocketTimeoutException) {
                    timeoutExceptionHappenSingleLiveEvent.setValue(context.getResources().getString(R.string.timeout_exception_happen_message));
                } else {
                    Log.e(TAG, t.getMessage(), t);
                }
            }
        });
    }

    public void fetchProductResult(String path, String userLoginKey, int customerID) {
        sipSupporterService.getProductResult(path, userLoginKey, customerID).enqueue(new Callback<CustomerProductResult>() {
            @Override
            public void onResponse(Call<CustomerProductResult> call, Response<CustomerProductResult> response) {
                if (response.isSuccessful()) {
                    customerProductResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        CustomerProductResult customerProductResult = gson.fromJson(response.errorBody().string(), CustomerProductResult.class);
                        if (Integer.valueOf(customerProductResult.getErrorCode()) <= -9001) {
                            dangerousUserSingleLiveEvent.setValue(true);
                        } else {
                            errorCustomerProductResultSingleLiveEvent.setValue(customerProductResult.getError());
                        }
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<CustomerProductResult> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    noConnection.setValue(t.getMessage());
                } else if (t instanceof SocketTimeoutException) {
                    timeoutExceptionHappenSingleLiveEvent.setValue(context.getResources().getString(R.string.timeout_exception_happen_message));
                } else {
                    Log.e(TAG, t.getMessage(), t);
                }
            }
        });
    }

    public void postProductInfo(String path, String userLoginKey, ProductInfo productInfo) {
        sipSupporterService.postProductInfo(path, userLoginKey, productInfo).enqueue(new Callback<ProductResult>() {
            @Override
            public void onResponse(Call<ProductResult> call, Response<ProductResult> response) {
                if (response.isSuccessful()) {
                    productResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        ProductResult productResult = gson.fromJson(response.errorBody().string(), ProductResult.class);
                        if (Integer.valueOf(productResult.getErrorCode()) <= -9001) {
                            dangerousUserSingleLiveEvent.setValue(true);
                        } else {
                            errorProductResultSingleLiveEvent.setValue(productResult.getError());
                        }
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<ProductResult> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    noConnection.setValue(t.getMessage());
                } else if (t instanceof SocketTimeoutException) {
                    timeoutExceptionHappenSingleLiveEvent.setValue(context.getResources().getString(R.string.timeout_exception_happen_message));
                } else {
                    Log.e(TAG, t.getMessage(), t);
                }
            }
        });
    }

    public void fetchProductResult(String path, String userLoginKey) {
        sipSupporterService.getProductResult(path, userLoginKey).enqueue(new Callback<ProductResult>() {
            @Override
            public void onResponse(Call<ProductResult> call, Response<ProductResult> response) {
                if (response.isSuccessful()) {
                    getProductResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        ProductResult productResult = gson.fromJson(response.errorBody().string(), ProductResult.class);
                        if (Integer.valueOf(productResult.getErrorCode()) <= -9001) {
                            dangerousUserSingleLiveEvent.setValue(true);
                        } else {
                            getErrorProductResultSingleLiveEvent.setValue(productResult.getError());
                        }
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<ProductResult> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    noConnection.setValue(t.getMessage());
                } else if (t instanceof SocketTimeoutException) {
                    timeoutExceptionHappenSingleLiveEvent.setValue(context.getResources().getString(R.string.timeout_exception_happen_message));
                } else {
                    Log.e(TAG, t.getMessage(), t);
                }
            }
        });
    }

    public void postCustomerProducts(String path, String userLoginKey, CustomerProducts customerProducts) {
        sipSupporterService.postCustomerProducts(path, userLoginKey, customerProducts).enqueue(new Callback<CustomerProductResult>() {
            @Override
            public void onResponse(Call<CustomerProductResult> call, Response<CustomerProductResult> response) {
                if (response.isSuccessful()) {
                    PostCustomerProductsSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        CustomerProductResult customerProductResult = gson.fromJson(response.errorBody().string(), CustomerProductResult.class);
                        if (Integer.valueOf(customerProductResult.getErrorCode()) <= -9001) {
                            dangerousUserSingleLiveEvent.setValue(true);
                        } else {
                            errorPostCustomerProductsSingleLiveEvent.setValue(customerProductResult.getError());
                        }
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<CustomerProductResult> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    noConnection.setValue(t.getMessage());
                } else if (t instanceof SocketTimeoutException) {
                    timeoutExceptionHappenSingleLiveEvent.setValue(context.getResources().getString(R.string.timeout_exception_happen_message));
                } else {
                    Log.e(TAG, t.getMessage(), t);
                }
            }
        });
    }

    public void fetchProductInfo(String path, String userLoginKey, int productID) {
        sipSupporterService.getProductInfo(path, userLoginKey, productID).enqueue(new Callback<ProductResult>() {
            @Override
            public void onResponse(Call<ProductResult> call, Response<ProductResult> response) {
                if (response.isSuccessful()) {
                    productInfoSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        ProductResult productResult = gson.fromJson(response.errorBody().string(), ProductResult.class);
                        if (Integer.valueOf(productResult.getErrorCode()) <= -9001) {
                            dangerousUserSingleLiveEvent.setValue(true);
                        } else {
                            errorProductInfoSingleLiveEvent.setValue(productResult.getError());
                        }
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<ProductResult> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    noConnection.setValue(t.getMessage());
                } else if (t instanceof SocketTimeoutException) {
                    timeoutExceptionHappenSingleLiveEvent.setValue(context.getResources().getString(R.string.timeout_exception_happen_message));
                } else {
                    Log.e(TAG, t.getMessage(), t);
                }
            }
        });
    }

    public void deleteCustomerProduct(String path, String userLoginKey, int customerProductID) {
        sipSupporterService.deleteCustomerProduct(path, userLoginKey, customerProductID).enqueue(new Callback<CustomerProductResult>() {
            @Override
            public void onResponse(Call<CustomerProductResult> call, Response<CustomerProductResult> response) {
                if (response.isSuccessful()) {
                    deleteCustomerProductSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        CustomerProductResult customerProductResult = gson.fromJson(response.errorBody().string(), CustomerProductResult.class);
                        if (Integer.valueOf(customerProductResult.getErrorCode()) <= -9001) {
                            dangerousUserSingleLiveEvent.setValue(true);
                        } else {
                            errorDeleteCustomerProductSingleLiveEvent.setValue(customerProductResult.getError());
                        }
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<CustomerProductResult> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    noConnection.setValue(t.getMessage());
                } else if (t instanceof SocketTimeoutException) {
                    timeoutExceptionHappenSingleLiveEvent.setValue(context.getResources().getString(R.string.timeout_exception_happen_message));
                } else {
                    Log.e(TAG, t.getMessage(), t);
                }
            }
        });
    }

    public void editCustomerProduct(String path, String userLoginKey, CustomerProducts customerProducts) {
        sipSupporterService.editCustomerProduct(path, userLoginKey, customerProducts).enqueue(new Callback<CustomerProductResult>() {
            @Override
            public void onResponse(Call<CustomerProductResult> call, Response<CustomerProductResult> response) {
                if (response.isSuccessful()) {
                    editCustomerProductSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        CustomerProductResult customerProductResult = gson.fromJson(response.errorBody().string(), CustomerProductResult.class);
                        if (Integer.valueOf(customerProductResult.getErrorCode()) <= -9001) {
                            dangerousUserSingleLiveEvent.setValue(true);
                        } else {
                            errorEditCustomerProductSingleLiveEvent.setValue(customerProductResult.getError());
                        }
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<CustomerProductResult> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    noConnection.setValue(t.getMessage());
                } else if (t instanceof SocketTimeoutException) {
                    timeoutExceptionHappenSingleLiveEvent.setValue(context.getResources().getString(R.string.timeout_exception_happen_message));
                } else {
                    Log.e(TAG, t.getMessage(), t);
                }
            }
        });
    }

    public void attach(String path, String userLoginKey, AttachInfo attachInfo) {
        sipSupporterService.attach(path, userLoginKey, attachInfo).enqueue(new Callback<AttachResult>() {
            @Override
            public void onResponse(Call<AttachResult> call, Response<AttachResult> response) {
                if (response.isSuccessful()) {
                    attachResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        AttachResult attachResult = gson.fromJson(response.errorBody().string(), AttachResult.class);
                        if (Integer.valueOf(attachResult.getErrorCode()) <= -9001) {
                            dangerousUserSingleLiveEvent.setValue(true);
                        } else {
                            errorAttachResultSingleLiveEvent.setValue(attachResult.getError());
                        }
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<AttachResult> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    noConnection.setValue(t.getMessage());
                } else if (t instanceof SocketTimeoutException) {
                    timeoutExceptionHappenSingleLiveEvent.setValue(context.getResources().getString(R.string.timeout_exception_happen_message));
                } else {
                    Log.e(TAG, t.getMessage(), t);
                }
            }
        });
    }

    public void fetchCustomerPaymentResult(String path, String userLoginKey, int customerID) {
        sipSupporterService.getCustomerPaymentResult(path, userLoginKey, customerID).enqueue(new Callback<CustomerPaymentResult>() {
            @Override
            public void onResponse(Call<CustomerPaymentResult> call, Response<CustomerPaymentResult> response) {
                if (response.isSuccessful()) {
                    customerPaymentResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        CustomerPaymentResult customerPaymentResult = gson.fromJson(response.errorBody().string(), CustomerPaymentResult.class);
                        if (Integer.valueOf(customerPaymentResult.getErrorCode()) <= -9001) {
                            dangerousUserSingleLiveEvent.setValue(true);
                        } else {
                            errorCustomerPaymentResultSingleLiveEvent.setValue(customerPaymentResult.getError());
                        }
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<CustomerPaymentResult> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    noConnection.setValue(t.getMessage());
                } else if (t instanceof SocketTimeoutException) {
                    timeoutExceptionHappenSingleLiveEvent.setValue(context.getResources().getString(R.string.timeout_exception_happen_message));
                } else {
                    Log.e(TAG, t.getMessage(), t);
                }
            }
        });
    }

    public void getAttachmentFilesViaCustomerPaymentID(String path, String userLoginKey, int customerPaymentID, boolean LoadFileData) {
        sipSupporterService.getAttachmentFilesViaCustomerPaymentID(path, userLoginKey, customerPaymentID, LoadFileData).enqueue(new Callback<AttachResult>() {
            @Override
            public void onResponse(Call<AttachResult> call, Response<AttachResult> response) {
                if (response.isSuccessful()) {
                    getAttachmentFilesViaCustomerPaymentIDSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        AttachResult attachResult = gson.fromJson(response.errorBody().string(), AttachResult.class);
                        if (Integer.valueOf(attachResult.getErrorCode()) <= -9001) {
                            dangerousUserSingleLiveEvent.setValue(true);
                        } else {
                            getErrorAttachmentFilesViaCustomerPaymentIDSingleLiveEvent.setValue(attachResult.getError());
                        }
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<AttachResult> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    noConnection.setValue(t.getMessage());
                } else if (t instanceof SocketTimeoutException) {
                    timeoutExceptionHappenSingleLiveEvent.setValue(context.getResources().getString(R.string.timeout_exception_happen_message));
                } else {
                    Log.e(TAG, t.getMessage(), t);
                }
            }
        });
    }

    public void addCustomerPayments(String path, String userLoginKey, CustomerPaymentInfo customerPaymentInfo) {
        sipSupporterService.addCustomerPaymentsResult(path, userLoginKey, customerPaymentInfo).enqueue(new Callback<CustomerPaymentResult>() {
            @Override
            public void onResponse(Call<CustomerPaymentResult> call, Response<CustomerPaymentResult> response) {
                if (response.isSuccessful()) {
                    addCustomerPaymentsSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        AttachResult attachResult = gson.fromJson(response.errorBody().string(), AttachResult.class);
                        if (Integer.valueOf(attachResult.getErrorCode()) <= -9001) {
                            dangerousUserSingleLiveEvent.setValue(true);
                        } else {
                            errorAddCustomerPaymentSingleLiveEvent.setValue(attachResult.getError());
                        }
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<CustomerPaymentResult> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    noConnection.setValue(t.getMessage());
                } else if (t instanceof SocketTimeoutException) {
                    timeoutExceptionHappenSingleLiveEvent.setValue(context.getResources().getString(R.string.timeout_exception_happen_message));
                } else {
                    Log.e(TAG, t.getMessage(), t);
                }
            }
        });
    }

    public void editCustomerPayments(String path, String userLoginKey, CustomerPaymentInfo customerPaymentInfo) {
        sipSupporterService.editCustomerPaymentsResult(path, userLoginKey, customerPaymentInfo).enqueue(new Callback<CustomerPaymentResult>() {
            @Override
            public void onResponse(Call<CustomerPaymentResult> call, Response<CustomerPaymentResult> response) {
                if (response.isSuccessful()) {
                    editCustomerPaymentsSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        AttachResult attachResult = gson.fromJson(response.errorBody().string(), AttachResult.class);
                        if (Integer.valueOf(attachResult.getErrorCode()) <= -9001) {
                            dangerousUserSingleLiveEvent.setValue(true);
                        } else {
                            errorEditCustomerPaymentSingleLiveEvent.setValue(attachResult.getError());
                        }
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<CustomerPaymentResult> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    noConnection.setValue(t.getMessage());
                } else if (t instanceof SocketTimeoutException) {
                    timeoutExceptionHappenSingleLiveEvent.setValue(context.getResources().getString(R.string.timeout_exception_happen_message));
                } else {
                    Log.e(TAG, t.getMessage(), t);
                }
            }
        });
    }

    public void deleteCustomerPayments(String path, String userLoginKey, int customerPaymentID) {
        sipSupporterService.deleteCustomerPayments(path, userLoginKey, customerPaymentID).enqueue(new Callback<CustomerPaymentResult>() {
            @Override
            public void onResponse(Call<CustomerPaymentResult> call, Response<CustomerPaymentResult> response) {
                if (response.isSuccessful()) {
                    deleteCustomerPaymentsSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        AttachResult attachResult = gson.fromJson(response.errorBody().string(), AttachResult.class);
                        if (Integer.valueOf(attachResult.getErrorCode()) <= -9001) {
                            dangerousUserSingleLiveEvent.setValue(true);
                        } else {
                            errorDeleteCustomerPaymentSingleLiveEvent.setValue(attachResult.getError());
                        }
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<CustomerPaymentResult> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    noConnection.setValue(t.getMessage());
                } else if (t instanceof SocketTimeoutException) {
                    timeoutExceptionHappenSingleLiveEvent.setValue(context.getResources().getString(R.string.timeout_exception_happen_message));
                } else {
                    Log.e(TAG, t.getMessage(), t);
                }
            }
        });
    }

    public void fetchBankAccounts(String path, String userLoginKey) {
        sipSupporterService.getBankAccountResult(path, userLoginKey).enqueue(new Callback<BankAccountResult>() {
            @Override
            public void onResponse(Call<BankAccountResult> call, Response<BankAccountResult> response) {
                if (response.isSuccessful()) {
                    bankAccountsResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        BankAccountResult bankAccountResult = gson.fromJson(response.errorBody().string(), BankAccountResult.class);
                        if (Integer.valueOf(bankAccountResult.getErrorCode()) <= -9001) {
                            dangerousUserSingleLiveEvent.setValue(true);
                        } else {
                            errorBankAccountsResultSingleLiveEvent.setValue(bankAccountResult.getError());
                        }
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<BankAccountResult> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    noConnection.setValue(t.getMessage());
                } else if (t instanceof SocketTimeoutException) {
                    timeoutExceptionHappenSingleLiveEvent.setValue(context.getResources().getString(R.string.timeout_exception_happen_message));
                } else {
                    Log.e(TAG, t.getMessage(), t);
                }
            }
        });
    }

    public void fetchFileWithCustomerProductID(String path, String userLoginKey, int customerProductID, boolean LoadFileData) {
        sipSupporterService.getAttachmentFilesViaCustomerProductID(path, userLoginKey, customerProductID, LoadFileData).enqueue(new Callback<AttachResult>() {
            @Override
            public void onResponse(Call<AttachResult> call, Response<AttachResult> response) {
                if (response.isSuccessful()) {
                    getAttachmentFilesViaCustomerProductIDSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        AttachResult attachResult = gson.fromJson(response.errorBody().string(), AttachResult.class);
                        if (Integer.valueOf(attachResult.getErrorCode()) <= -9001) {
                            dangerousUserSingleLiveEvent.setValue(true);
                        } else {
                            getErrorAttachmentFilesViaCustomerProductIDSingleLiveEvent.setValue(attachResult.getError());
                        }
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<AttachResult> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    noConnection.setValue(t.getMessage());
                } else if (t instanceof SocketTimeoutException) {
                    timeoutExceptionHappenSingleLiveEvent.setValue(context.getResources().getString(R.string.timeout_exception_happen_message));
                } else {
                    Log.e(TAG, t.getMessage(), t);
                }
            }
        });
    }

    public void fetchFileWithCustomerSupportID(String path, String userLoginKey, int customerSupportID, boolean LoadFileData) {
        sipSupporterService.getAttachmentFilesViaCustomerSupportID(path, userLoginKey, customerSupportID, LoadFileData).enqueue(new Callback<AttachResult>() {
            @Override
            public void onResponse(Call<AttachResult> call, Response<AttachResult> response) {
                if (response.isSuccessful()) {
                    getAttachmentFilesViaCustomerSupportIDSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        AttachResult attachResult = gson.fromJson(response.errorBody().string(), AttachResult.class);
                        if (Integer.valueOf(attachResult.getErrorCode()) <= -9001) {
                            dangerousUserSingleLiveEvent.setValue(true);
                        } else {
                            getErrorAttachmentFilesViaCustomerSupportIDSingleLiveEvent.setValue(attachResult.getError());
                        }
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<AttachResult> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    noConnection.setValue(t.getMessage());
                } else if (t instanceof SocketTimeoutException) {
                    timeoutExceptionHappenSingleLiveEvent.setValue(context.getResources().getString(R.string.timeout_exception_happen_message));
                } else {
                    Log.e(TAG, t.getMessage(), t);
                }
            }
        });
    }

    public void fetchWithAttachID(String path, String userLoginKey, int attachID, boolean loadFileData) {
        sipSupporterService.getAttachmentFileViaAttachID(path, userLoginKey, attachID, loadFileData).enqueue(new Callback<AttachResult>() {
            @Override
            public void onResponse(Call<AttachResult> call, Response<AttachResult> response) {
                if (response.isSuccessful()) {
                    attachResultViaAttachIDSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        AttachResult attachResult = gson.fromJson(response.errorBody().string(), AttachResult.class);
                        if (Integer.valueOf(attachResult.getErrorCode()) <= -9001) {
                            dangerousUserSingleLiveEvent.setValue(true);
                        } else {
                            errorAttachResultViaAttachIDSingleLiveEvent.setValue(attachResult.getError());
                        }
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<AttachResult> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    noConnection.setValue(t.getMessage());
                } else if (t instanceof SocketTimeoutException) {
                    timeoutExceptionHappenSingleLiveEvent.setValue(context.getResources().getString(R.string.timeout_exception_happen_message));
                } else {
                    Log.e(TAG, t.getMessage(), t);
                }
            }
        });
    }

    public void fetchPaymentsListByBankAccounts(String path, String userLoginKey, int bankAccountID) {
        sipSupporterService.paymentsListByBankAccount(path, userLoginKey, bankAccountID).enqueue(new Callback<PaymentResult>() {
            @Override
            public void onResponse(Call<PaymentResult> call, Response<PaymentResult> response) {
                if (response.isSuccessful()) {
                    paymentResultPaymentsListByBankAccountSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        PaymentResult paymentResult = gson.fromJson(response.errorBody().string(), PaymentResult.class);
                        if (Integer.valueOf(paymentResult.getErrorCode()) <= -9001) {
                            dangerousUserSingleLiveEvent.setValue(true);
                        } else {
                            errorPaymentResultPaymentsListByBankAccountSingleLiveEvent.setValue(paymentResult.getError());
                        }
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<PaymentResult> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    noConnection.setValue(t.getMessage());
                } else if (t instanceof SocketTimeoutException) {
                    timeoutExceptionHappenSingleLiveEvent.setValue(context.getResources().getString(R.string.timeout_exception_happen_message));
                } else {
                    Log.e(TAG, t.getMessage(), t);
                }
            }
        });
    }

    public void paymentsEdit(String path, String userLoginKey, PaymentInfo paymentInfo) {
        sipSupporterService.paymentsEdit(path, userLoginKey, paymentInfo).enqueue(new Callback<PaymentResult>() {
            @Override
            public void onResponse(Call<PaymentResult> call, Response<PaymentResult> response) {
                if (response.isSuccessful()) {
                    paymentResultPaymentsEditSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        PaymentResult paymentResult = gson.fromJson(response.errorBody().string(), PaymentResult.class);
                        if (Integer.valueOf(paymentResult.getErrorCode()) <= -9001) {
                            dangerousUserSingleLiveEvent.setValue(true);
                        } else {
                            errorPaymentResultPaymentsEditSingleLiveEvent.setValue(paymentResult.getError());
                        }
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<PaymentResult> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    noConnection.setValue(t.getMessage());
                } else if (t instanceof SocketTimeoutException) {
                    timeoutExceptionHappenSingleLiveEvent.setValue(context.getResources().getString(R.string.timeout_exception_happen_message));
                } else {
                    Log.e(TAG, t.getMessage(), t);
                }
            }
        });
    }

    public void paymentsDelete(String path, String userLoginKey, int paymentID) {
        sipSupporterService.paymentsDelete(path, userLoginKey, paymentID).enqueue(new Callback<PaymentResult>() {
            @Override
            public void onResponse(Call<PaymentResult> call, Response<PaymentResult> response) {
                if (response.isSuccessful()) {
                    paymentResultPaymentsDeleteSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        PaymentResult paymentResult = gson.fromJson(response.errorBody().string(), PaymentResult.class);
                        if (Integer.valueOf(paymentResult.getErrorCode()) <= -9001) {
                            dangerousUserSingleLiveEvent.setValue(true);
                        } else {
                            errorPaymentResultPaymentsDeleteSingleLiveEvent.setValue(paymentResult.getError());
                        }
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<PaymentResult> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    noConnection.setValue(t.getMessage());
                } else if (t instanceof SocketTimeoutException) {
                    timeoutExceptionHappenSingleLiveEvent.setValue(context.getResources().getString(R.string.timeout_exception_happen_message));
                } else {
                    Log.e(TAG, t.getMessage(), t);
                }
            }
        });
    }

    public void fetchPaymentSubjects(String path, String userLoginKey) {
        sipSupporterService.paymentSubjectsList(path, userLoginKey).enqueue(new Callback<PaymentSubjectResult>() {
            @Override
            public void onResponse(Call<PaymentSubjectResult> call, Response<PaymentSubjectResult> response) {
                if (response.isSuccessful()) {
                    paymentSubjectResultPaymentSubjectsListSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        PaymentSubjectResult paymentSubjectResult = gson.fromJson(response.errorBody().string(), PaymentSubjectResult.class);
                        if (Integer.valueOf(paymentSubjectResult.getErrorCode()) <= -9001) {
                            dangerousUserSingleLiveEvent.setValue(true);
                        } else {
                            errorPaymentSubjectResultPaymentSubjectsListSingleLiveEvent.setValue(paymentSubjectResult.getError());
                        }
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<PaymentSubjectResult> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    noConnection.setValue(t.getMessage());
                } else if (t instanceof SocketTimeoutException) {
                    timeoutExceptionHappenSingleLiveEvent.setValue(context.getResources().getString(R.string.timeout_exception_happen_message));
                } else {
                    Log.e(TAG, t.getMessage(), t);
                }
            }
        });
    }

    public void paymentsAdd(String path, String userLoginKey, PaymentInfo paymentInfo) {
        sipSupporterService.paymentsAdd(path, userLoginKey, paymentInfo).enqueue(new Callback<PaymentResult>() {
            @Override
            public void onResponse(Call<PaymentResult> call, Response<PaymentResult> response) {
                if (response.isSuccessful()) {
                    paymentResultPaymentsAddSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        PaymentSubjectResult paymentSubjectResult = gson.fromJson(response.errorBody().string(), PaymentSubjectResult.class);
                        if (Integer.valueOf(paymentSubjectResult.getErrorCode()) <= -9001) {
                            dangerousUserSingleLiveEvent.setValue(true);
                        } else {
                            errorPaymentResultPaymentsAddSingleLiveEvent.setValue(paymentSubjectResult.getError());
                        }
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<PaymentResult> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    noConnection.setValue(t.getMessage());
                } else if (t instanceof SocketTimeoutException) {
                    timeoutExceptionHappenSingleLiveEvent.setValue(context.getResources().getString(R.string.timeout_exception_happen_message));
                } else {
                    Log.e(TAG, t.getMessage(), t);
                }
            }
        });
    }

    public void deleteAttach(String path, String userLoginKey, int attachID) {
        sipSupporterService.deleteAttach(path, userLoginKey, attachID).enqueue(new Callback<AttachResult>() {
            @Override
            public void onResponse(Call<AttachResult> call, Response<AttachResult> response) {
                if (response.isSuccessful()) {
                    deleteAttachResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        AttachResult attachResult = gson.fromJson(response.errorBody().string(), AttachResult.class);
                        if (Integer.valueOf(attachResult.getErrorCode()) <= -9001) {
                            dangerousUserSingleLiveEvent.setValue(true);
                        } else {
                            errorDeleteAttachResultSingleLiveEvent.setValue(attachResult.getError());
                        }
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<AttachResult> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    noConnection.setValue(t.getMessage());
                } else if (t instanceof SocketTimeoutException) {
                    timeoutExceptionHappenSingleLiveEvent.setValue(context.getResources().getString(R.string.timeout_exception_happen_message));
                } else {
                    Log.e(TAG, t.getMessage(), t);
                }
            }
        });
    }

    public void fetchAttachmentsByPaymentID(String path, String userLoginKey, int paymentID, boolean LoadFileData) {
        sipSupporterService.getAttachmentListByPaymentID(path, userLoginKey, paymentID, LoadFileData).enqueue(new Callback<AttachResult>() {
            @Override
            public void onResponse(Call<AttachResult> call, Response<AttachResult> response) {
                if (response.isSuccessful()) {
                    attachmentListResultByPaymentID.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        AttachResult attachResult = gson.fromJson(response.errorBody().string(), AttachResult.class);
                        if (Integer.valueOf(attachResult.getErrorCode()) <= -9001) {
                            dangerousUserSingleLiveEvent.setValue(true);
                        } else {
                            errorAttachmentListResultByPaymentID.setValue(attachResult.getError());
                        }
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<AttachResult> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    noConnection.setValue(t.getMessage());
                } else if (t instanceof SocketTimeoutException) {
                    timeoutExceptionHappenSingleLiveEvent.setValue(context.getResources().getString(R.string.timeout_exception_happen_message));
                } else {
                    Log.e(TAG, t.getMessage(), t);
                }
            }
        });
    }

    public void fetchPaymentSubjectInfo(String path, String userLoginKey, int paymentSubjectID) {
        sipSupporterService.paymentInfo(path, userLoginKey, paymentSubjectID).enqueue(new Callback<PaymentSubjectResult>() {
            @Override
            public void onResponse(Call<PaymentSubjectResult> call, Response<PaymentSubjectResult> response) {
                if (response.isSuccessful()) {
                    paymentSubjectInfoResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        AttachResult attachResult = gson.fromJson(response.errorBody().string(), AttachResult.class);
                        if (Integer.valueOf(attachResult.getErrorCode()) <= -9001) {
                            dangerousUserSingleLiveEvent.setValue(true);
                        } else {
                            errorPaymentSubjectInfoResultSingleLiveEvent.setValue(attachResult.getError());
                        }
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<PaymentSubjectResult> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    noConnection.setValue(t.getMessage());
                } else if (t instanceof SocketTimeoutException) {
                    timeoutExceptionHappenSingleLiveEvent.setValue(context.getResources().getString(R.string.timeout_exception_happen_message));
                } else {
                    Log.e(TAG, t.getMessage(), t);
                }
            }
        });
    }
}
