package dev.jvoyatz.modern.android.network.config;

import com.google.common.reflect.TypeToken;
import com.google.common.truth.Truth;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;

import dev.jvoyatz.modern.android.network.config.model.ApiResponse;
import dev.jvoyatz.modern.android.network.utils.StringConverterFactory;
import kotlin.Unit;
import kotlinx.coroutines.Deferred;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;

/** @noinspection rawtypes*/
public class ApiResponseCallAdapterFactoryTest {
    @Rule
    public final MockWebServer server = new MockWebServer();

    private final CallAdapter.Factory factory = ApiResponseCallAdapterFactory.get();
    private Retrofit retrofit;

    @Before
    public void setUp() {
        retrofit = new Retrofit.Builder()
                .baseUrl(server.url("/"))
                .addConverterFactory(new StringConverterFactory())
                .addCallAdapterFactory(factory)
                .build();
    }


    @Test
    public void givenARandomRawType_returnsNull() {
        //given
        Type type = new TypeToken<String>() {}.getType();

        //when
        CallAdapter<?, ?> callAdapter = factory.get(type, new Annotation[0], retrofit);

        //then
        Truth.assertThat(callAdapter).isNull();
    }

    @Test
    public void givenADeferredRandomRawType_returnsNull() {
        //given
        Type type = new TypeToken<Deferred<String>>() {}.getType();

        //when
        CallAdapter<?, ?> callAdapter = factory.get(type, new Annotation[0], retrofit);

        //then
        Truth.assertThat(callAdapter).isNull();
    }

    @Test
    public void givenAListRandomRawType_returnsNull() {
        //given
        Type type = new TypeToken<List<String>>() {}.getType();

        //when
        CallAdapter<?, ?> callAdapter = factory.get(type, new Annotation[0], retrofit);

        //then
        Truth.assertThat(callAdapter).isNull();
    }

    @Test
    public void givenRawDeferred_returnsNull() {
        //given
        Type type = new TypeToken<Deferred>() {}.getType();

        //when
        CallAdapter<?, ?> callAdapter = factory.get(type, new Annotation[0], retrofit);

        //then
        Truth.assertThat(callAdapter).isNull();
    }

    @Test
    public void givenRawDeferredApiResponse_returnsNull() {
        //given
        Type type = new TypeToken<Deferred<ApiResponse>>() {}.getType();

        //when
        CallAdapter<?, ?> callAdapter = factory.get(type, new Annotation[0], retrofit);

        //then
        Truth.assertThat(callAdapter).isNull();
    }

    @Test
    public void givenRawApiResponse_returnsNull() {
        //given
        Type type = new TypeToken<ApiResponse>() {}.getType();

        //when
        CallAdapter<?, ?> callAdapter = factory.get(type, new Annotation[0], retrofit);

        //then
        Truth.assertThat(callAdapter).isNull();
    }



    @Test
    public void givenParameterizedApiResponse_returnsNull() {
        //given
        Type type = new TypeToken<ApiResponse<String, Unit>>() {}.getType();

        //when
        CallAdapter<?, ?> callAdapter = factory.get(type, new Annotation[0], retrofit);

        //then
        Truth.assertThat(callAdapter).isNull();
    }

    @Test
    public void givenCallApiResponse_withStringSuccessTypeToken_responseTypeIsString(){
        //given
        Type type = new TypeToken<Call<ApiResponse<String, Unit>>>() {}.getType();

        //when
        CallAdapter<?, ?> callAdapter = factory.get(type, new Annotation[0], retrofit);
        Type responseType = callAdapter.responseType();

        //then
        Truth.assertThat(responseType).isEqualTo(String.class);
    }
    @Test
    public void givenDeferredApiResponse_withStringSuccessTypeToken_responseTypeIsString(){
        //given
        Type type = new TypeToken<Deferred<ApiResponse<String, Unit>>>() {}.getType();

        //when
        CallAdapter<?, ?> callAdapter = factory.get(type, new Annotation[0], retrofit);
        Type responseType = callAdapter.responseType();

        //then
        Truth.assertThat(responseType).isEqualTo(String.class);
    }

    @Test
    public void givenCallApiResponse_withExtendsStringSuccessTypeToken_responseTypeIsString(){
        //given
        Type type = new TypeToken<Call<ApiResponse<? extends String, Unit>>>() {}.getType();

        //when
        CallAdapter<?, ?> callAdapter = factory.get(type, new Annotation[0], retrofit);
        Type responseType = callAdapter.responseType();

        //then
        Truth.assertThat(responseType).isEqualTo(String.class);
    }

    @Test
    public void givenDeferredApiResponse_withExtendsStringSuccessTypeToken_responseTypeIsString(){
        //given
        Type type = new TypeToken<Deferred<ApiResponse<String, Unit>>>() {}.getType();

        //when
        CallAdapter<?, ?> callAdapter = factory.get(type, new Annotation[0], retrofit);
        Type responseType = callAdapter.responseType();

        //then
        Truth.assertThat(responseType).isEqualTo(String.class);
    }

    @Test
    public void givenCallApiResponse_withListStringSuccessTypeToken_responseTypeIsString(){
        //given
        Type type = new TypeToken<Call<ApiResponse<List<String>, Unit>>>() {}.getType();

        //when
        CallAdapter<?, ?> callAdapter = factory.get(type, new Annotation[0], retrofit);
        Type responseType = callAdapter.responseType();

        //then
        Truth.assertThat(responseType).isEqualTo(new TypeToken<List<String>>() {}.getType());
    }

    @Test
    public void givenDeferredApiResponse_withListStringSuccessTypeToken_responseTypeIsString(){
        //given
        Type type = new TypeToken<Deferred<ApiResponse<List<String>, Unit>>>() {}.getType();

        //when
        CallAdapter<?, ?> callAdapter = factory.get(type, new Annotation[0], retrofit);
        Type responseType = callAdapter.responseType();

        //then
        Truth.assertThat(responseType).isEqualTo(new TypeToken<List<String>>() {}.getType());
    }
}
