package com.example.sipsupporterapp.retrofit;

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
import com.example.sipsupporterapp.model.SupportEventResult;
import com.example.sipsupporterapp.model.UserLoginParameter;
import com.example.sipsupporterapp.model.UserResult;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SipSupporterService {

    @GET("{path}")
    Call<DateResult> fetchDate(@Path("path") String path, @Header("userLoginKey") String userLoginKey);

    @POST("{path}")
    Call<UserResult> login(@Path("path") String path, @Body UserLoginParameter userLoginParameter);

    @POST("{path}")
    Call<UserResult> changePassword(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Body String newPassword);

    @GET("{path}")
    Call<CustomerResult> fetchCustomers(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Query("customerName") String customerName);

    @GET("{path}")
    Call<CustomerSupportResult> fetchCustomerSupports(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Query("customerID") int customerID);

    @POST("{path}")
    Call<CustomerSupportResult> addCustomerSupport(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Body CustomerSupportInfo customerSupportInfo);

    @GET("{path}")
    Call<SupportEventResult> fetchSupportEvents(@Path("path") String path, @Header("userLoginKey") String userLoginKey);

    @GET("{path}")
    Call<CustomerUserResult> fetchCustomerUsers(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Query("customerID") int customerID);

    @GET("{path}")
    Call<CustomerProductResult> fetchCustomerProducts(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Query("customerID") int customerID);

    @POST("{path}")
    Call<ProductResult> addProduct(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Body ProductInfo productInfo);

    @GET("{path}")
    Call<ProductResult> fetchProducts(@Path("path") String path, @Header("userLoginKey") String userLoginKey);

    @GET("{path}")
    Call<ProductResult> fetchProductInfo(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Query("productID") int productID);

    @POST("{path}")
    Call<CustomerProductResult> addCustomerProduct(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Body CustomerProducts customerProducts);

    @PUT("{path}")
    Call<CustomerProductResult> editCustomerProduct(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Body CustomerProducts customerProducts);

    @DELETE("{path}")
    Call<CustomerProductResult> deleteCustomerProduct(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Query("customerProductID") int customerProductID);

    @GET("{path}")
    Call<CustomerPaymentResult> fetchCustomerPayments(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Query("customerID") int customerID);

    @POST("{path}")
    Call<CustomerPaymentResult> addCustomerPayment(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Body CustomerPaymentInfo customerPaymentInfo);

    @PUT("{path}")
    Call<CustomerPaymentResult> editCustomerPayment(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Body CustomerPaymentInfo customerPaymentInfo);

    @DELETE("{path}")
    Call<CustomerPaymentResult> deleteCustomerPayment(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Query("customerPaymentID") int customerPaymentID);

    @GET("{path}")
    Call<AttachResult> fetchCustomerPaymentAttachments(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Query("customerPaymentID") int paymentID, @Query("LoadFileData") boolean LoadFileData);

    @GET("{path}")
    Call<AttachResult> fetchCustomerProductAttachments(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Query("customerProductID") int customerProductID, @Query("LoadFileData") boolean LoadFileData);

    @GET("{path}")
    Call<AttachResult> fetchCustomerSupportAttachments(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Query("customerSupportID") int customerSupportID, @Query("LoadFileData") boolean LoadFileData);

    @GET("{path}")
    Call<AttachResult> fetchPaymentAttachments(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Query("paymentID") int paymentID, @Query("LoadFileData") boolean LoadFileData);

    @POST("{path}")
    Call<AttachResult> attach(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Body AttachInfo attachInfo);

    @DELETE("{path}")
    Call<AttachResult> deleteAttach(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Query("attachID") int attachID);

    @GET("{path}")
    Call<AttachResult> fetchAttachInfo(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Query("attachID") int attachID, @Query("LoadFileData") boolean LoadFileData);

    @GET("{path}")
    Call<PaymentSubjectResult> fetchPaymentSubjects(@Path("path") String path, @Header("userLoginKey") String userLoginKey);

    @GET("{path}")
    Call<PaymentResult> fetchPayments(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Query("bankAccountID") int bankAccountID);

    @GET("{path}")
    Call<PaymentSubjectResult> fetchPaymentInfo(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Query("paymentSubjectID") int paymentSubjectID);

    @POST("{path}")
    Call<PaymentResult> addPayment(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Body PaymentInfo paymentInfo);

    @PUT("{path}")
    Call<PaymentResult> editPayment(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Body PaymentInfo paymentInfo);

    @DELETE("{path}")
    Call<PaymentResult> deletePayment(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Query("paymentID") int paymentID);

    @GET("{path}")
    Call<BankAccountResult> fetchBankAccounts(@Path("path") String path, @Header("userLoginKey") String userLoginKey);
}
