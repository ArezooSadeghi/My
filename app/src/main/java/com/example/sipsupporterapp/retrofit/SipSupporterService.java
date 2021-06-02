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

    @POST("{path}")
    Call<UserResult> postUserLoginParameter(@Path("path") String path, @Body UserLoginParameter userLoginParameter);

    @GET("{path}")
    Call<CustomerResult> getCustomers(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Query("customerName") String customerName);

    @POST("{path}")
    Call<UserResult> changePassword(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Body String newPassword);

    @GET("{path}")
    Call<CustomerSupportResult> getCustomerSupportResult(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Query("customerID") int customerID);

    @GET("{path}")
    Call<CustomerUserResult> getCustomerUserResult(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Query("customerID") int customerID);

    @GET("{path}")
    Call<SupportEventResult> getSupportEventResult(@Path("path") String path, @Header("userLoginKey") String userLoginKey);

    @POST("{path}")
    Call<CustomerSupportResult> postCustomerSupportInfo(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Body CustomerSupportInfo customerSupportInfo);

    @GET("{path}")
    Call<DateResult> getDateResult(@Path("path") String path, @Header("userLoginKey") String userLoginKey);

    @GET("{path}")
    Call<CustomerProductResult> getProductResult(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Query("customerID") int customerID);

    @POST("{path}")
    Call<ProductResult> postProductInfo(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Body ProductInfo productInfo);

    @GET("{path}")
    Call<ProductResult> getProductResult(@Path("path") String path, @Header("userLoginKey") String userLoginKey);

    @POST("{path}")
    Call<CustomerProductResult> postCustomerProducts(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Body CustomerProducts customerProducts);

    @GET("{path}")
    Call<ProductResult> getProductInfo(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Query("productID") int productID);

    @DELETE("{path}")
    Call<CustomerProductResult> deleteCustomerProduct(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Query("customerProductID") int customerProductID);

    @PUT("{path}")
    Call<CustomerProductResult> editCustomerProduct(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Body CustomerProducts customerProducts);

    @POST("{path}")
    Call<AttachResult> attach(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Body AttachInfo attachInfo);

    @GET("{path}")
    Call<CustomerPaymentResult> getCustomerPaymentResult(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Query("customerID") int customerID);

    @GET("{path}")
    Call<AttachResult> getAttachmentFilesViaCustomerPaymentID(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Query("customerPaymentID") int paymentID, @Query("LoadFileData") boolean LoadFileData);

    @GET("{path}")
    Call<AttachResult> getAttachmentFilesViaCustomerProductID(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Query("customerProductID") int customerProductID, @Query("LoadFileData") boolean LoadFileData);

    @GET("{path}")
    Call<AttachResult> getAttachmentFilesViaCustomerSupportID(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Query("customerSupportID") int customerSupportID, @Query("LoadFileData") boolean LoadFileData);

    @GET("{path}")
    Call<AttachResult> getAttachmentFileViaAttachID(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Query("attachID") int attachID, @Query("LoadFileData") boolean LoadFileData);

    @PUT("{path}")
    Call<CustomerPaymentResult> editCustomerPaymentsResult(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Body CustomerPaymentInfo customerPaymentInfo);

    @POST("{path}")
    Call<CustomerPaymentResult> addCustomerPaymentsResult(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Body CustomerPaymentInfo customerPaymentInfo);

    @DELETE("{path}")
    Call<CustomerPaymentResult> deleteCustomerPayments(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Query("customerPaymentID") int customerPaymentID);

    @GET("{path}")
    Call<BankAccountResult> getBankAccountResult(@Path("path") String path, @Header("userLoginKey") String userLoginKey);

    @GET("{path}")
    Call<PaymentResult> paymentsListByBankAccount(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Query("bankAccountID") int bankAccountID);

    @PUT("{path}")
    Call<PaymentResult> paymentsEdit(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Body PaymentInfo paymentInfo);

    @DELETE("{path}")
    Call<PaymentResult> paymentsDelete(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Query("paymentID") int paymentID);

    @GET("{path}")
    Call<PaymentSubjectResult> paymentSubjectsList(@Path("path") String path, @Header("userLoginKey") String userLoginKey);

    @POST("{path}")
    Call<PaymentResult> paymentsAdd(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Body PaymentInfo paymentInfo);

    @DELETE("{path}")
    Call<AttachResult> deleteAttach(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Query("attachID") int attachID);

    @GET("{path}")
    Call<AttachResult> getAttachmentListByPaymentID(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Query("paymentID") int paymentID, @Query("LoadFileData") boolean LoadFileData);

    @GET("{path}")
    Call<PaymentSubjectResult> paymentInfo(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Query("paymentSubjectID") int paymentSubjectID);
}
