package ro.vodafone.mcare.android.ui.activities.support;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.rest.observers.RequestSessionObserver;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.ui.activities.ChatBubbleActivity;
import ro.vodafone.mcare.android.ui.utils.rxactivityresult.ActivityResult;
import ro.vodafone.mcare.android.ui.utils.rxactivityresult.RxActivityResult;
import ro.vodafone.mcare.android.utils.D;
import ro.vodafone.mcare.android.utils.PhotoUtils;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.utils.VodafoneNotificationManager;
import ro.vodafone.mcare.android.widget.messaging.message.Message;
import ro.vodafone.mcare.android.widget.messaging.message.MessageSource;
import ro.vodafone.mcare.android.widget.messaging.message.TextMessage;
import ro.vodafone.mcare.android.widget.messaging.message.messageItem.MessageItem;
import ro.vodafone.mcare.android.widget.messaging.message.messageItem.MessageRecyclerAdapter;
import ro.vodafone.mcare.android.widget.messaging.utils.CustomSettings;
import ro.vodafone.mcare.android.widget.messaging.utils.DateUtils;
import ro.vodafone.mcare.android.widget.messaging.view.ViewUtils;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by cristi on 24/06/2017.
 * .
 */

@SuppressLint("ViewConstructor")
public class SlyceMessagingView extends MyChatView implements View.OnClickListener {

	public static final long MAX_FILE_DIM = 3145728;
	public static final String TAG = "SlyceMessagingView";
	private static final int START_RELOADING_DATA_AT_SCROLL_VALUE = 5000;
	final SupportWindow window;
	public int unreadMessagesCount = 0;

	boolean endMessageOnce = true;
	String myName = "You";
	String agentName = "Vodafone";
	String defaultUserId;
	String defaultDisplayName;
	String lastReadTime = "0";

	ImageView mSendButton;
	ImageView mSnapButton;
	EditText mEntryField;
	RelativeLayout slyceInputID;

	RecyclerView mRecyclerView;
	View rootView;

	MultipartBody.Part messagePart;
	MessageRecyclerAdapter mRecyclerAdapter;
	CustomSettings customSettings;

	int startHereWhenUpdate;
	long recentUpdatedTime;
	File file;

	boolean responseClosedOnce = true;
	int lastSessionMessagesCount = 0;
	private RequestSessionObserver<Response<GeneralResponse<StartChatResponse>>> startChatObserver = new RequestSessionObserver<Response<GeneralResponse<StartChatResponse>>>() {
		@Override
		public void onNext(Response<GeneralResponse<StartChatResponse>> bigResponse) {

			GeneralResponse<StartChatResponse> response = bigResponse.body();
			if (response.getTransactionStatus() == 2 && response.getTransactionFault() != null) {
				switch (response.getTransactionFault().getFaultCode()) {
					case "EC05305":
//                        D.e();
						window.stopLoadingDialog();
						window.inflateError("Momentan serviciul de chat este indisponibil din motive tehnice. Lucrăm la remedierea lor. ", true);
						break;
					case "EC00099":
//                        D.e();
						window.stopLoadingDialog();
						window.inflateError("Momentan serviciul de chat este indisponibil din motive tehnice. Lucrăm la remedierea lor. ", true);
						break;
					case "EC05306":
//                        D.e();
						window.stopLoadingDialog();
						window.inflateError("Momentan, operatorii pentru această categorie sunt indisponibili. ", true);
						break;
					case "EC05307":
//                        D.e();
//                        D.e()
						window.stopLoadingDialog();
						window.inflateError("Datorită numărului mare de solicitări, momentan serviciul de chat nu este disponibil. ", true);
						break;
					case "EC05308":
//                        D.e();
						window.stopLoadingDialog();
						window.inflateError("Datorită numărului mare de solicitări, momentan serviciul de chat nu este disponibil. ", true);
						break;
					case "EC05309":
//                        D.e();
						window.stopLoadingDialog();
						window.inflateError("Există deja o conversație în desfășurare care folosește această adresă de email. Poți începe o nouă conversație după închiderea celei curente. ", true);
						break;
					default:
//                        D.e("response.getTransactionFault()" + response.getTransactionFault().getFaultCode());
//                        D.e("response.getTransactionFault()" + response.getTransactionFault().getFaultMessage());
						window.stopLoadingDialog();
						window.inflateError();
						break;
				}
			} else if (response.getTransactionStatus() == 0 && response.getTransactionSuccess() != null) {
				VodafoneController.getInstance().setChatConnected(true);
				unreadMessagesCount = 0;

				ChatBubbleActivity.sessionKey = response.getTransactionSuccess().getSessionKey();
				ChatBubbleActivity.contactId = response.getTransactionSuccess().getContactId();
				ChatBubbleActivity.wlpjSessionId = response.getTransactionSuccess().getWLPJSESSIONID();
				ChatBubbleSingleton.getInstance().setWLPJSESSIONID(response.getTransactionSuccess().getWLPJSESSIONID());

				window.getChatService().updateChatState(1).subscribe(new RequestSessionObserver<String>() {
					@Override
					public void onNext(String response) {
//                        D.w("response = " + response);
					}

					@Override
					public void onCompleted() {
						super.onCompleted();
					}

					@Override
					public void onError(Throwable e) {
						super.onError(e);
						D.e("e = " + e);
					}
				});

				initAgent();
			}
		}

		@Override
		public void onCompleted() {
			super.onCompleted();
//            D.w();
		}


		@Override
		public void onError(Throwable e) {
			super.onError(e);
//            Log.e(TAG, "onError -> Error when start chat: " + e);
			D.e("e = " + e);
			window.stopLoadingDialog();
			window.inflateError();
		}
	};

	public SlyceMessagingView(@NonNull final SupportWindow w) {
		super(w.getContext());
		window = w;

		if (!VodafoneController.getInstance().isChatConnected()) {
			ChatBubbleSingleton.getInstance().setAgentResponseEmptyOnce(true);
		} else {
			ChatBubbleSingleton.getInstance().setAgentResponseEmptyOnce(false);
		}

		myName = window.firstName + " " + window.lastName;
		window.showLoadingDialog();
		window.disableGOPCButton(false);
		window.hideFaqSearchButton();

		VodafoneController.getInstance().setChatRequest(window.startChatRequest);
		VodafoneController.getInstance().setChatService(window.getChatService());

		//Tealium Track View
		Map<String, Object> tealiumMapView = new HashMap(6);
		tealiumMapView.put("screen_name", "chat session");
		tealiumMapView.put("journey_name", "help&support");
		tealiumMapView.put("user_type", VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
		TealiumHelper.trackView("screen_name", tealiumMapView);

		ChatSessionTrackingEvent event = new ChatSessionTrackingEvent();
		TrackingAppMeasurement journey = new TrackingAppMeasurement();
		journey.event8 = "event8";
		journey.getContextData().put("event8", journey.event8);
		event.defineTrackingProperties(journey);
		VodafoneController.getInstance().getTrackingService().trackCustom(event);

		//this.rootView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_slyce_messaging, this);

		this.customSettings = new CustomSettings();

		// Setup views
		slyceInputID = window.findViewById(R.id.slyceInputID);
		slyceInputID.setVisibility(View.VISIBLE);

		mEntryField = window.findViewById(R.id.slyce_messaging_edit_text_entry_field);
		mEntryField.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				try {
					mEntryField.setBackground(ContextCompat.getDrawable(VodafoneController.currentActivity(), R.drawable.empty));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		mSendButton = window.findViewById(R.id.slyce_messaging_image_view_send);
		mSnapButton = window.findViewById(R.id.slyce_messaging_image_view_snap);

		mEntryField.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
		mEntryField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					//Clear focus here from edittext
					closeKeyboard();
					mEntryField.clearFocus();
				}
				return false;
			}
		});
		// Add interfaces
		mSendButton.setOnClickListener(this);
		mSnapButton.setOnClickListener(this);

		// Init variables for recycler view
		LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getContext()) {

			@Override
			public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
				try {
					super.onLayoutChildren(recycler, state);
				} catch (Exception e) {
					D.e("okso = " + e);
					e.printStackTrace();
				}
			}
		};

		mRecyclerView = window.findViewById(R.id.recyclerView);
		mRecyclerView.setNestedScrollingEnabled(true);
		mRecyclerAdapter = new MessageRecyclerAdapter(mRecyclerView, customSettings, window.views.scrollView);

		// Setup recycler view
		mRecyclerView.setLayoutManager(mLinearLayoutManager);
		mRecyclerView.setAdapter(mRecyclerAdapter);

		startUpdateTimestampsThread();
		startHereWhenUpdate = 0;
		recentUpdatedTime = 0;

		window.onChatStart();

		disableSendMessages();

		ChatBubbleSingleton.getInstance().setMinimized(false, false);

		if (!VodafoneController.getInstance().isChatConnected()) {
			window.getChatService().postStartChat(VodafoneController.getInstance().getUserProfile().getMsisdn(),
					window.startChatRequest).subscribe(startChatObserver);
		} else {

			if (VodafoneController.getInstance().isConversationOpen()) {
				initMessageListener();
				enableSendMessages();
			} else
				initAgent();

			window.initialMessageCounter = getTotalNotifications();
			VodafoneController.getInstance().setBadgeCount(0);
			updateBadge();
			VodafoneNotificationManager.cancelNotification(1, getContext());

			window.disableGOPCButton(VodafoneController.getInstance().isConversationOpen());
			window.setBottomNavigationMenuVisibility(false);
			window.stopLoadingDialog();

			mRecyclerView.scrollToPosition(ChatBubbleSingleton.getInstance().getSavedMessageItemsListSize() - 1);

		}
	}

	private void unsubscribeAgentObservable() {
		if (ChatBubbleSingleton.getInstance().getWaitingForAgent() != null &&
				!ChatBubbleSingleton.getInstance().getWaitingForAgent().isUnsubscribed()) {
			ChatBubbleSingleton.getInstance().getWaitingForAgent().unsubscribe();
			ChatBubbleSingleton.getInstance().setWaitingForAgent(null);
		}
	}

	private void unsubscribeMessagesObservable() {
		if (ChatBubbleSingleton.getInstance().getWaitingForMessages() != null &&
				!ChatBubbleSingleton.getInstance().getWaitingForMessages().isUnsubscribed()) {
			ChatBubbleSingleton.getInstance().getWaitingForMessages().unsubscribe();
			ChatBubbleSingleton.getInstance().setWaitingForMessages(null);
		}
	}

	private void closeKeyboard() {
		InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(getWindowToken(), 0);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.slyce_messaging_image_view_send) {
			sendUserTextMessage();
		} else if (v.getId() == R.id.slyce_messaging_image_view_snap) {

			window.mightBeAClose = false;

			if (ContextCompat.checkSelfPermission(getContext(),
					Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
					ContextCompat.checkSelfPermission(getContext(),
							Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
				ActivityCompat.requestPermissions(window.activity, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 232);
			} else {


				mEntryField.setText("");

				final File mediaStorageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
				final String fname = "img_" + System.currentTimeMillis() + ".jpg";

				file = new File(mediaStorageDir, fname);

				Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.setType("*/*");      //all files
				intent.addCategory(Intent.CATEGORY_OPENABLE);
				Intent chooserIntent = Intent.createChooser(intent, "Take a photo or select one from your device");
				chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{takePhotoIntent});

				try {
					RxActivityResult.startActivityForResult(window.activity, chooserIntent, 11).take(1).subscribe(new Action1<ActivityResult>() {
						@Override
						public void call(ActivityResult activityResult) {
							onActivityResult(activityResult.getRequestCode(), activityResult.getResultCode(), activityResult.getData());
						}
					});
				} catch (RuntimeException exception) {
					D.e(exception.getMessage());
					exception.printStackTrace();
				}
			}
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 232 && data == null)
			return;

		try {
			if (requestCode == 11 && resultCode == Activity.RESULT_OK) {
				file = null;

				Uri selectedImageUri = data.getData();
				if (selectedImageUri == null)
					selectedImageUri = getUriFromDataBundleBitmap(data);


				//selectedImageBmp = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);

				file = new File(getContext().getFilesDir(), "file." + getExtension(selectedImageUri));

				InputStream input = getContext().getContentResolver().openInputStream(selectedImageUri);

				FileUtils.copyInputStreamToFile(input, file);

				input.close();

				//String path = getPath(getContext(), selectedImageUri); // UrisyntaxException

				//file = new File(path);

				if (!isValidExtension(file)) {
					file = null;
					displayErrorMessage(" Se pot încarca doar urmatoarele tipuri de fișiere: .jpeg, Excel, Word, .jpg, .png, .pdf.  ");
					ChatBubbleSingleton.getInstance().setConversationMessagesObservableRestarted(false);
				} else if (file.length() > MAX_FILE_DIM) {
					file = null;
					displayErrorMessage(" Dimensiunea maxima a fișierului trebuie sa fie 3 MB. ");
					ChatBubbleSingleton.getInstance().setConversationMessagesObservableRestarted(false);
				} else {
					RequestBody requestFile =
							RequestBody.create(
									MediaType.parse(getContext().getContentResolver().getType(selectedImageUri)), file);

					messagePart = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

//                    D.w("sendMediaMessage activity.contactId = " + ChatBubbleActivity.contactId);
//                    D.w("sendMediaMessage activity.sessionKey = " + ChatBubbleActivity.sessionKey);

					new ChatService(VodafoneController.currentActivity()).sendMediaMessage(ChatBubbleActivity.contactId, ChatBubbleActivity.sessionKey, messagePart).subscribe(new RequestSessionObserver<String>() {
						@Override
						public void onNext(String response) {
							window.mightBeAClose = true;
//                            D.d("sendMediaMessage   response = " + response);
						}

						@Override
						public void onCompleted() {
							super.onCompleted();
						}

						@Override
						public void onError(Throwable e) {
							super.onError(e);
							D.e("e = " + e);
							if (e instanceof NullPointerException || e instanceof FileNotFoundException) {
								file = null;
								displayErrorMessage(" Ne pare rău, fișierul nu a putut fi încărcat. Vă rog să reincercati. ");
								ChatBubbleSingleton.getInstance().setConversationMessagesObservableRestarted(false);
							}
						}
					});
				}
			}
		} catch (Exception e) {
			D.e("okso = " + e);
			e.printStackTrace();
			file = null;
			displayErrorMessage(" Ne pare rău, fișierul nu a putut fi încărcat. Vă rog să reincercati. ");
			ChatBubbleSingleton.getInstance().setConversationMessagesObservableRestarted(false);
		}
	}

	public String getExtension(Uri uri) {
		String extension;

		//Check uri format to avoid null
		if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
			//If scheme is a content
			final MimeTypeMap mime = MimeTypeMap.getSingleton();
			extension = mime.getExtensionFromMimeType(getContext().getContentResolver().getType(uri));
		} else {
			//If scheme is a File
			//This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
			extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());

		}

		return extension;
	}

	public String getPath(Context context, Uri uri) throws URISyntaxException {
		final boolean needToCheckUri = Build.VERSION.SDK_INT >= 19;
		String selection = null;
		String[] selectionArgs = null;
		// Uri is different in versions after KITKAT (Android 4.4), we need to
		// deal with different Uris.
		if (needToCheckUri && DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {
			if (isExternalStorageDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				return Environment.getExternalStorageDirectory() + "/" + split[1];
			} else if (isDownloadsDocument(uri)) {
				final String id = DocumentsContract.getDocumentId(uri);
				uri = ContentUris.withAppendedId(
						Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
			} else if (isMediaDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];
				if ("image".equals(type)) {
					uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}
				selection = "_id=?";
				selectionArgs = new String[]{split[1]};
			}
		}
		if ("content".equalsIgnoreCase(uri.getScheme())) {

			// Return the remote address
//            if (isGooglePhotosUri(uri) || isGoogleDriveUri(uri)) {
//                return uri.getPath();
//                String mimeType = context.getContentResolver().getType(uri);
//                Cursor returnCursor =
//                        context.getContentResolver().query(uri, null, null, null, null);
//                int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
//                int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
//                returnCursor.moveToFirst();
//                String nameString = returnCursor.getString(nameIndex);
//                String sizeString = Long.toString(returnCursor.getLong(sizeIndex));
//            }

			String[] projection = {MediaStore.Images.Media.DATA};
			Cursor cursor = null;
			try {
				cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
				int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

				if (cursor.moveToFirst())
					return cursor.getString(column_index);

			} catch (Exception ignored) {
			} finally {
				if (cursor != null) {
					cursor.close();
				}
			}
		} else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}
		return null;

	}

	public Uri getUriFromDataBundleBitmap(Intent data) {

		Bundle bundle = data.getExtras();

		Object bundleObject = null;
		Uri uri = null;
		Bitmap b = null;

		for (String key : bundle.keySet()) {
			bundleObject = bundle.get(key);
			break;
		}

		if (bundleObject instanceof Uri) {
			uri = (Uri) bundleObject;
			return uri;
		} else if (bundleObject instanceof Bitmap) {
			b = (Bitmap) bundleObject;

			Uri bitmapUri = Uri.parse(PhotoUtils.storeImage(getContext().getContentResolver(), b, ("VDF_IMG_" + String.valueOf(System.currentTimeMillis())), ""));

//            D.w("uri3 = " + bitmapUri);
			return bitmapUri;
		}

		return null;
	}

	public boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri.getAuthority());
	}

	public boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri.getAuthority());
	}

	public boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri.getAuthority());
	}

	public boolean isGooglePhotosUri(Uri uri) {
		return "com.google.android.apps.photos.content".equals(uri.getAuthority());
	}

	private boolean isGoogleDriveUri(Uri uri) {
		return "com.google.android.apps.docs.storage".equals(uri.getAuthority());
	}

	public int geLastSessiontMessagesCount() {
		return lastSessionMessagesCount;
	}

	public void addNewMessage(Message message) {
		lastSessionMessagesCount++;
		mRecyclerAdapter.updateMessageItemDataList(message.toMessageItem(getContext()));
	}


	private void updateChatState() {
		//Todo Create a subscription window.getChatService().pollMessages and unsubscribe it when init Listners

		window.getChatService().updateChatState(2).subscribe(new RequestSessionObserver<String>() {
			@Override
			public void onNext(String response) {
//                D.w("response = " + response);
			}

			@Override
			public void onCompleted() {
				super.onCompleted();
			}

			@Override
			public void onError(Throwable e) {
				super.onError(e);
				D.e("e = " + e);
			}
		});
	}

	private void initAgent() {
		unsubscribeAgentObservable();

		Subscription waitingForAgent = Observable.interval(2, TimeUnit.SECONDS)
				.subscribeOn(Schedulers.newThread())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Observer<Long>() {
					@Override
					public void onNext(Long aLong) {

//                        D.d("THIS ID = " + this);
						//user post the message and waits the agent to response
						window.getChatService().postStartPoll(
								ChatBubbleActivity.contactId,
								ChatBubbleActivity.sessionKey,
								window.email,
								false,
								lastReadTime,
								window.channel)
								.subscribe(new RequestSessionObserver<String>() {
									@Override
									public void onNext(String response) {

										if (response != null && !response.isEmpty()) {
											try {
												response = Html.fromHtml(response).toString();
												JSONObject obj = new JSONObject(response);
												processAgentResponse(obj);

											} catch (JSONException e) {
												Log.e(TAG, "Error in getting agent response in JSON: " + e.getMessage());
											}
										} else {
											processOnHold();
										}
									}

									@Override
									public void onCompleted() {
										super.onCompleted();
									}

									@Override
									public void onError(Throwable e) {
										super.onError(e);
										D.e("e = " + e);
									}
								});
					}

					@Override
					public void onCompleted() {
//                        D.w();
					}

					@Override
					public void onError(Throwable e) {
						D.e("e =" + e);
					}
				});

		ChatBubbleSingleton.getInstance().setWaitingForAgent(waitingForAgent);
	}

	public void initMessageListener() {
		unsubscribeMessagesObservable();
		ChatBubbleSingleton.getInstance().setConversationMessagesObservableRestarted(true);

		Subscription waitingForMessages = Observable.interval(2, TimeUnit.SECONDS)
				.subscribeOn(Schedulers.newThread())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new RequestSessionObserver<Long>() {
					@Override
					public void onNext(Long aLong) {
//                        D.d("THIS ID = " + this);
						window.getChatService().pollMessages(
								ChatBubbleActivity.contactId,
								ChatBubbleActivity.sessionKey,
								false,
								false,
								lastReadTime,
								window.channel)
								.subscribe(new RequestSessionObserver<String>() {
									@Override
									public void onNext(String response) {
										if (response != null && !response.equals("")) {
											try {
												response = Html.fromHtml(response).toString();
												//D.w(" pollMessages response = " + response);
												JSONObject obj = new JSONObject(response);

												if (response.contains("Conversaţia a fost închisă")) {
													endMessageOnce = false;
													generateEndMessage();
													stop(ChatBubbleSingleton.getInstance().getWaitingForMessages(), false);
													return;
												}

												processReceivedMessage(obj);
												processAgentClosed(obj);

											} catch (JSONException je) {
												D.w(" pollMessages JSONException = " + je);

												if (endMessageOnce) {
													endMessageOnce = false;
													generateEndMessage();
													stop(ChatBubbleSingleton.getInstance().getWaitingForMessages(), false);
												}


											} catch (Exception e) {
												D.e("pollMessages Exception = " + e);
											}
										}
										//Todo  Hardcode to receive messages
                                        /*else
                                            {
                                            try {
                                                JSONObject obj = new JSONObject(getHardCodedResponse());
                                                processReceivedMessage(obj);
                                            }catch (Exception e){
                                                e.printStackTrace();
                                            }
                                        }*/
									}

									@Override
									public void onCompleted() {
										super.onCompleted();
//                                        D.w("pollMessages onCompleted");
									}

									@Override
									public void onError(Throwable e) {
										super.onError(e);
										D.e("pollMessages e = " + e);
									}
								});
					}

					@Override
					public void onCompleted() {
//                        D.w();
					}

					@Override
					public void onError(Throwable e) {
						super.onError(e);
						D.e("e = " + e);
					}

				});

		ChatBubbleSingleton.getInstance().setWaitingForMessages(waitingForMessages);
	}

	private String getHardCodedResponse() {
		return "{\"lastReadTime\": 1506338103156,\"messages\":\"[Daniel Fifiita] Buna! Sunt Prodan Pavel. Cu ce te pot ajuta?(field_sep)25-09-2017 02:15:03[msg_sep]\"}";
	}

	public void stop(rx.Subscription s, boolean enterPollMessages) {
		if (s != null)
			s.unsubscribe();
		if (enterPollMessages) {
			updateChatState();
			initMessageListener();
		}
	}

	public void processOnHold() {
		if (ChatBubbleSingleton.getInstance().isAgentResponseEmptyOnce()) {

			generateStartMessage();

			D.w("processOnHold");
			if (window != null)
				window.setBottomNavigationMenuVisibility(false);
			ChatBubbleSingleton.getInstance().setAgentResponseEmptyOnce(false);
		}
	}

	private void processReceivedOnHoldAngentMessage(String messageText) {
		TextMessage message = new TextMessage();
		message.setDate(System.currentTimeMillis());
		message.setSource(MessageSource.EXTERNAL_USER);
		message.setDisplayName(defaultDisplayName);
		message.setText(messageText);
		message.setUserId(defaultUserId);
		message.ignoreNotifications = true;

		addNewMessage(message);
	}

	boolean agentResponsedOnce = false;

	private String getDisplayNameOfMessageIndex(int index) {
		try {

			return ChatBubbleSingleton.getInstance()
					.getSavedMessageItemsList().get(index)
					.getMessage().getDisplayName();

		} catch (Exception e) {
			return null;
		}
	}

	private long getLastMessageDate(int index) {
		try {

			return ChatBubbleSingleton.getInstance()
					.getSavedMessageItemsList().get(index)
					.getMessage().getDate();

		} catch (Exception e) {
			return -1;
		}
	}

	private void processAgentResponse(JSONObject obj) {
		//check response onHold
		if (obj.has("onHoldMessages")) {
			try {
				if (!agentResponsedOnce)
					processReceivedOnHoldAngentMessage(obj.getString("onHoldMessages"));
				agentResponsedOnce = true;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		try {
			if (obj.get("lastReadTime") != null && obj.get("messages") != null) {
				VodafoneController.getInstance().setConversationOpen(true);
//                ChatBubbleSingleton.getInstance().setMinimized(true);

				lastReadTime = obj.getString("lastReadTime");
				String messages = obj.getString("messages");


				window.stopLoadingDialog();
				window.disableGOPCButton(true);
				window.setBottomNavigationMenuVisibility(false);

				String d = "msg_sep]";
				String[] messagesArray = messages.split(d);

				for (int i = 0; i < messagesArray.length; i++) {

					String name;
					String message;
					long date;

					try {
						name = displayName(messagesArray[i]);
						message = displayMessage(messagesArray[i]);
						Calendar calendar = dateStringToCalendar(getDate(messagesArray[i]));
						date = (calendar == null) ? System.currentTimeMillis() : calendar.getTimeInMillis();
					} catch (Exception e) {
						D.e("e = " + e);
						name = agentName;
						message = messagesArray[i].substring(0, messagesArray[i].indexOf("field_sep") - 1);
						date = System.currentTimeMillis();
					}

//                    message = message.replaceAll("]", "");

					int numberOfSavedMessages = ChatBubbleSingleton.getInstance().getSavedMessageItemsList().size();

					String lastMessageDisplayName = getDisplayNameOfMessageIndex(numberOfSavedMessages - 1);

					long lastMessageDate = getLastMessageDate(numberOfSavedMessages - 1);

					if (lastMessageDisplayName != null && name.equals(lastMessageDisplayName) && date == lastMessageDate) {
						continue;
					}

					if (!name.equals(myName)) {
						agentName = name;
						this.displayAgentTextMessage(name, message, date);
					} else {
						this.displayUserTextMessage(name, message, date);
					}

					if (ChatBubbleSingleton.getInstance().getMinimized()) {
						unreadMessagesCount++;
						updateBadge();
					}
					showNotification(name, message);

				}
			}

			this.enableSendMessages();
			this.stop(ChatBubbleSingleton.getInstance().getWaitingForAgent(), true);
			ChatBubbleSingleton.getInstance().setConversationMessagesObservableRestarted(false);
		} catch (Exception e) {
			Log.e(TAG, "processAgentResponse " + e);
		}

		//stop/close chat if there are any error messages in agent response
		this.processAgentClosed(obj);
	}

	public void processAgentClosed(final JSONObject obj) {
		if (obj.has("errorMessage")) {
			if (responseClosedOnce) {
				generateAgentClosedMessage();
				responseClosedOnce = false;
				this.stop(ChatBubbleSingleton.getInstance().getWaitingForAgent(), false);
			}
		}
	}

    /*public void displayUserTextMessage(String name, String text) {
        final TextMessage message = new TextMessage();
        message.setDate(System.currentTimeMillis());
        message.setSource(MessageSource.LOCAL_USER);
        message.setDisplayName(myName);
        message.setText(text);
        message.setMessageType(MessageType.OPEN_CONVERSATION_MESSAGE);
        message.ignoreNotifications = true;
        message.setUserId(defaultUserId);

        addNewMessage(message);

        ChatBubbleSingleton.getInstance().incrementNumberOfMessagesInActiveChat();
    }*/

	private void sendUserTextMessage() {
		String text = ViewUtils.getStringFromEditText(mEntryField);
		if (TextUtils.isEmpty(text))
			return;
		mEntryField.setText("");

//        ChatBubbleSingleton.getInstance().incrementNumberOfMessagesInActiveChat();
//
//        final TextMessage message = new TextMessage();
//        message.setDate(System.currentTimeMillis());
//        message.setSource(MessageSource.LOCAL_USER);
//        message.setDisplayName(myName);
//        message.setText(text);
//        message.ignoreNotifications = true;
//        message.setUserId(defaultUserId);
//
//        addNewMessage(message);

		window.getChatService().sendTextMessage(ChatBubbleActivity.contactId,
				ChatBubbleActivity.sessionKey,
				new VodafoneMessage(window.firstName, window.lastName, text),
				false,
				lastReadTime,
				window.channel)
				.subscribe(new RequestSessionObserver<String>() {
					@Override
					public void onNext(String response) {
						response = response.replaceAll("\n", "");
//                        D.w("SEND MESSAGE response = *" + response + "*");
						if (response.length() > 1) {
							if (endMessageOnce) {
								endMessageOnce = false;
								generateEndMessage();
								stop(ChatBubbleSingleton.getInstance().getWaitingForMessages(), false);
							}
						}
					}

					@Override
					public void onCompleted() {
						super.onCompleted();
//                        D.w();
					}

					@Override
					public void onError(Throwable e) {
						super.onError(e);
						D.e("Error = " + e);
					}
				});
	}

	public void generateStartMessage() {
		TextMessage message = new TextMessage();
		message.setDate(System.currentTimeMillis());
		message.setSource(MessageSource.EXTERNAL_USER);
		message.setDisplayName(defaultDisplayName);
		message.setText(getResources().getString(R.string.chat_first_agent_message));
		message.setUserId(defaultUserId);
		message.ignoreNotifications = true;

		addNewMessage(message);

		window.stopLoadingDialog();
	}

	public void generateEndMessage() {

		TextMessage message = new TextMessage();
		message.setDate(System.currentTimeMillis());
		message.setSource(MessageSource.EXTERNAL_USER);
		message.setDisplayName(agentName);
		message.setText(getResources().getString(R.string.chat_agent_end_conversation));
		message.setUserId(defaultUserId);
		addNewMessage(message);

		if (ChatBubbleSingleton.getInstance().getMinimized()) {
			unreadMessagesCount++;
			updateBadge();
		}
		showNotification(agentName, message.getText());

		disableSendMessages();
		window.disableGOPCButton(false);

		VodafoneController.getInstance().setConversationOpen(false);
		window.updateChatBubbleForAgentClosedCase(false);

		ChatBubbleSingleton.getInstance().setClosedByAgentButNotLoggedOut(true);

		endMessageOnce = false;
	}

	public void generateAgentClosedMessage() {

		TextMessage message = new TextMessage();
		message.setDate(System.currentTimeMillis());
		message.setSource(MessageSource.EXTERNAL_USER);
		message.setDisplayName(myName);
		message.setText("Agentul a închis.");
		message.setUserId(defaultUserId);
		addNewMessage(message);

		if (ChatBubbleSingleton.getInstance().getMinimized()) {
			unreadMessagesCount++;
			updateBadge();
		}
		showNotification(myName, message.getText());

		disableSendMessages();
		window.disableGOPCButton(false);

		VodafoneController.getInstance().setConversationOpen(false);
		window.updateChatBubbleForAgentClosedCase(false);
		ChatBubbleSingleton.getInstance().setClosedByAgentButNotLoggedOut(true);

		responseClosedOnce = false;
	}

	public void disableSendMessages() {
		mSnapButton.setClickable(false);
		mEntryField.setEnabled(false);
		mEntryField.setInputType(InputType.TYPE_NULL);
	}

	public void enableSendMessages() {
		mSnapButton.setClickable(true);
		mEntryField.setEnabled(true);
		mEntryField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
		mEntryField.setMaxLines(6);
	}

	public String displayName(String message) {
		String[] temp = message.split("]");
		return temp[0].trim().replace("]", "").replace("[", "");
	}

	public String displayMessage(String message) {
		String[] temp2 = message.split("]", 2);

		int pos = temp2[1].trim().indexOf("field_sep");
		return temp2[1].trim().substring(0, pos - 1);
	}

	public String getDate(String message) {
		String[] tempSplitForDate = message.split("(field_sep)");
		return tempSplitForDate[1].trim().replace("[", "");
	}

	private Calendar dateStringToCalendar(String stringDateFormated) {
		try {

			SimpleDateFormat tf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

			// String formated "dd-MM-yyyy" to Calendar formated
			Calendar calendarFormated = Calendar.getInstance();
			if (stringDateFormated != null && !stringDateFormated.isEmpty()) {
				Date parseTime = tf.parse(stringDateFormated);
				calendarFormated.setTime(parseTime);
			}
			return calendarFormated;

		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	private void startUpdateTimestampsThread() {
		ScheduledExecutorService scheduleTaskExecutor = Executors.newScheduledThreadPool(1);
		scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				for (int i = startHereWhenUpdate; i < ChatBubbleSingleton.getInstance().getSavedMessageItemsList().size(); i++) {
					try {
						MessageItem messageItem = ChatBubbleSingleton.getInstance().getSavedMessageItemsList().get(i);
						Message message = messageItem.getMessage();
						if (DateUtils.dateNeedsUpdated(getContext(), message.getDate(), messageItem.getDate())) {
							messageItem.updateDate(getContext(), message.getDate());
							updateTimestampAtValue(i);
						} else if (i == startHereWhenUpdate) {
							i++;
						}
					} catch (RuntimeException exception) {
						Log.d("debug", exception.getMessage());
						exception.printStackTrace();
					}
				}
			}
		}, 0, 62, TimeUnit.SECONDS);
	}

	private void updateTimestampAtValue(final int i) {
		if (window != null) {
			post(new Runnable() {
				@Override
				public void run() {
					mRecyclerAdapter.notifyItemChanged(i);
				}
			});
		}
	}

	public void displayAgentTextMessage(String agentName, String text, long date) {
		try {
			ChatBubbleSingleton.getInstance().incrementNumberOfMessagesInActiveChat();

			TextMessage message = new TextMessage();
			message.setDate(date);
			message.setSource(MessageSource.EXTERNAL_USER);
			message.setDisplayName(agentName);
			message.setText(text);
			message.setUserId(defaultUserId);

			this.addNewMessage(message);

		} catch (Exception e) {
			Log.d(TAG, "Error when display Message");
		}
	}

	public void displayUserTextMessage(String myName, String text, long date) {
		try {
			ChatBubbleSingleton.getInstance().incrementNumberOfMessagesInActiveChat();

			TextMessage message = new TextMessage();
			message.setDate(date);
			message.setSource(MessageSource.LOCAL_USER);
			message.setDisplayName(myName);
			message.setText(text);
			message.ignoreNotifications = true;
			message.setUserId(defaultUserId);

			this.addNewMessage(message);
		} catch (Exception e) {
			Log.d(TAG, "Error when display Message");
		}
	}

	public void processReceivedMessage(JSONObject obj) {

		try {
			if (obj.get("lastReadTime") != null && obj.get("messages") != null) {
				lastReadTime = String.valueOf(obj.get("lastReadTime"));
				String messages = String.valueOf(obj.get("messages"));

				String d = "msg_sep]";
				String[] messagesArray = messages.split(d);

				int numberOfNewMessages = 0;

				Log.d(TAG, "processReceivedMessage: message array: " + messagesArray.length);
				Log.d(TAG, "processReceivedMessage: fromController: " + ChatBubbleSingleton.getInstance().getNumberOfMessagesInActiveChat());

				if (!ChatBubbleSingleton.getInstance().isConversationMessagesObservableRestarted()) {
					numberOfNewMessages = 0;
					Log.d(TAG, "processReceivedMessage: conversation not restarted ");
				} else {
					//Should use messageArray.length - 1 because after split we have 2 messages (last one is "")
					if (ChatBubbleSingleton.getInstance().getNumberOfMessagesInActiveChat() < messagesArray.length) {
						int diferenceBetweenSavedListAndIncomingMessages = messagesArray.length - ChatBubbleSingleton.getInstance().getNumberOfMessagesInActiveChat();
						numberOfNewMessages = diferenceBetweenSavedListAndIncomingMessages - 1;
						Log.d(TAG, "processReceivedMessage: numberOfMessages first if " + numberOfNewMessages);
					} else {
						if (ChatBubbleSingleton.getInstance().getNumberOfMessagesInActiveChat() >= messagesArray.length) {
							ChatBubbleSingleton.getInstance().setConversationMessagesObservableRestarted(false);
							return;
						}
						Log.d(TAG, "processReceivedMessage: return ");
					}

					Log.d(TAG, "processReceivedMessage: numberOfNewMessages inside  " + numberOfNewMessages);
				}

				Log.d(TAG, "processReceivedMessage: numberOfNewMessages outside  " + numberOfNewMessages);

				for (int i = numberOfNewMessages; i < messagesArray.length; i++) {

					Log.d(TAG, "processReceivedMessage: in cycle :  " + messagesArray[i]);
					Log.d(TAG, "processReceivedMessage: in cycle: " + messagesArray.length);

					String name = "";
					String message = "";
					long date;

					try {
						name = displayName(messagesArray[i]);
						message = displayMessage(messagesArray[i]);
						Calendar calendar = dateStringToCalendar(getDate(messagesArray[i]));
						date = (calendar == null) ? System.currentTimeMillis() : calendar.getTimeInMillis();
					} catch (Exception e) {
						name = agentName;
						message = messagesArray[i].substring(0, messagesArray[i].indexOf("field_sep") - 1);
						date = System.currentTimeMillis();
					}

//                    message = message.replaceAll("]", "");

					int numberOfSavedMessages = ChatBubbleSingleton.getInstance().getSavedMessageItemsList().size();

					String lastMessageDisplayName = getDisplayNameOfMessageIndex(numberOfSavedMessages - 1);

					long lastMessageDate = getLastMessageDate(numberOfSavedMessages - 1);

					if (lastMessageDisplayName != null && name.equals(lastMessageDisplayName) && date == lastMessageDate) {
						continue;
					}

					if (!name.equals(myName))
						displayAgentTextMessage(name, message, date);
					else
						displayUserTextMessage(name, message, date);

					if (ChatBubbleSingleton.getInstance().getMinimized()) {
						unreadMessagesCount++;
						updateBadge();
					}
					showNotification(name, message);
				}
			}

			ChatBubbleSingleton.getInstance().setConversationMessagesObservableRestarted(false);

		} catch (Exception e) {
			D.e("processReceivedMessage ERROR ~~~ " + e);
		}
	}

	/*
	 *
	 * Show chat notifications if app is in background;
	 * App is in backgroud when current activity is null.
	 *
	 * */
	private void showNotification(String name, String message) {
		Log.d(TAG, "showNotification");
		if (getContext() != null && VodafoneController.currentActivity() == null) {
			VodafoneController.getInstance().setBadgeCountFromChatBubble();
			VodafoneNotificationManager.sendNotification(getContext(),
					String.valueOf(getChatNotificationStringBuilder(name, message)), VodafoneController.getInstance().getBadgeCount());
		}
	}

	private StringBuilder getChatNotificationStringBuilder(String name, String message) {
		Log.d(TAG, "getChatNotificationStringBuilder");
		StringBuilder chatNotificationStringBuilder = new StringBuilder();
		chatNotificationStringBuilder.append("[");
		chatNotificationStringBuilder.append(name);
		chatNotificationStringBuilder.append("]");
		chatNotificationStringBuilder.append(message);
		return chatNotificationStringBuilder;
	}

	public int getTotalNotifications() {
		int retValue = 0;

		for (int i = 0; i < ChatBubbleSingleton.getInstance().getSavedMessageItemsList().size(); i++) {
			MessageItem m = ChatBubbleSingleton.getInstance().getSavedMessageItemsList().get(i);
			if (!m.getMessage().ignoreNotifications) {
				retValue++;
			}
		}
		Log.d(TAG, "getTotalNotifications , retValue :" + retValue);
		return retValue;
	}

	public void updateBadge() {
		if (ChatBubbleSingleton.getInstance().getMinimized()) {
			ChatBubbleSingleton.getInstance().setMessagesCount(getTotalNotifications() - window.initialMessageCounter);
			Log.d(TAG, "getTotalNotifications , retValue :" + window.initialMessageCounter);
//            D.w("VALUE = " + (getTotalNotifications() - window.initialMessageCounter));
		}
	}

	public int getMessageCount() {
		return ChatBubbleSingleton.getInstance().getNumberOfMessagesInActiveChat();
	}

	public boolean isValidExtension(File file) {

		String filename = file.getName();
		String filenameArray[] = filename.split("\\.");
		String extension = filenameArray[filenameArray.length - 1];
//        D.w(extension);
		//.jpeg, Excel, Word, .jpg, .png, .pdf.
		String[] array = {"jpeg", "xls", "doc", "docx", "xlsx", "jpg", "png", "pdf"};

		for (String anArray : array)
			if (extension.equalsIgnoreCase(anArray))
				return true;

		return false;
	}

	public void displayErrorMessage(String text) {
		TextMessage message = new TextMessage();
		message.setDate(System.currentTimeMillis());
		message.setSource(MessageSource.EXTERNAL_USER);
		message.setDisplayName(agentName);
		message.setText(text);
		message.setUserId(defaultUserId);

		addNewMessage(message);
	}

	private static class ChatSessionTrackingEvent extends TrackingEvent {

		@Override
		public void defineTrackingProperties(TrackingAppMeasurement s) {
			super.defineTrackingProperties(s);

			if (getErrorMessage() != null) {
				s.events = "event11";
				s.getContextData().put("event11", s.event11);
			}
			s.pageName = s.prop21 + "chat session";
			s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:" + "chat session");


			s.channel = "help&support";
			s.getContextData().put("&&channel", s.channel);
			s.eVar18 = "begin chat";
			s.getContextData().put("eVar18", s.eVar18);
			s.eVar19 = "query";
			s.getContextData().put("eVar19", s.eVar19);
			s.prop22 = "mcare:" + "chat session";
			s.getContextData().put("prop22", s.prop22);
		}
	}

}
