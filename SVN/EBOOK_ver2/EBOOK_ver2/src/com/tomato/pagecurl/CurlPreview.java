package com.tomato.pagecurl;



import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.RectF;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * OpenGL ES View.
 * 
 * @author harism
 */
/*onSizeChanged의 상위 클래스안에는 GLSurfaceView에있는것을 받는가 */
public class CurlPreview extends GLSurfaceView implements View.OnTouchListener,
CurlRenderer.Observer,View.OnLongClickListener {

	// Shows one page at the center of view.
	public static final int SHOW_ONE_PAGE = 1;
	// Shows two pages side by side.
	public static final int SHOW_TWO_PAGES = 2;
	// One page is the default.
	private int mViewMode = SHOW_ONE_PAGE;

	private boolean mRenderLeftPage = true;
	private boolean mAllowLastPageCurl = true;

	// Page meshes. Left and right meshes are 'static' while curl is used to
	// show page flipping.
	private CurlMesh mPageCurl;/*이게 젤중요하네*/
	private CurlMesh mPageLeft;/**/
	private CurlMesh mPageRight;/**/
	// Curl state. We are flipping none, left or right page.
	private static final int CURL_NONE = 0;
	private static final int CURL_LEFT = 1;
	private static final int CURL_RIGHT = 2;
	private int mCurlState = CURL_NONE;

	// Current page index. This is always showed on right page.
	private int mCurrentIndex = 0;/*0으로 초기화된 인덱스 페이지*/
	private int pageNum = 0;
	// Bitmap size. These are updated from renderer once it's initialized.
	private int mPageBitmapWidth = -1;
	private int mPageBitmapHeight = -1;

	// Start position for dragging.
	private PointF mDragStartPos = new PointF();
	private PointerPosition mPointerPos = new PointerPosition();
	private PointF mCurlPos = new PointF();
	private PointF mCurlDir = new PointF();

	private boolean mAnimate = false;
	private PointF mAnimationSource = new PointF();
	private PointF mAnimationTarget = new PointF();
	private long mAnimationStartTime;
	private long mAnimationDurationTime = 300;
	private int mAnimationTargetEvent;
	
	// Constants for mAnimationTargetEvent.
	private static final int SET_CURL_TO_LEFT = 1;
	private static final int SET_CURL_TO_RIGHT = 2;
	
	private CurlRenderer mRenderer; /*mRenderer를 여기서 셋팅*/
	private BitmapProvider mBitmapProvider;
	private SizeChangedObserver mSizeChangedObserver;

	private boolean mEnableTouchPressure = false;

	/**
	 * Default constructor.
	 */
	public CurlPreview(Context ctx) {
		super(ctx);
		init(ctx);
	}

	/**
	 * Default constructor.
	 */
	public CurlPreview(Context ctx, AttributeSet attrs) {
		super(ctx, attrs);
		init(ctx);
	}

	/**
	 * Default constructor.
	 */
	public CurlPreview(Context ctx, AttributeSet attrs, int defStyle) {
		this(ctx, attrs);
	}

	/**
	 * Set current page index.
	 */
	/*컬엑티비티에서 오버라이드해서 잘쓰고 잇음 여기선기냥 시체처럼 선언만해놧네*/
	public int getCurrentIndex() {
		return mCurrentIndex;						//GetしたIndexをPreviewに適用
	}
	
	@Override
	public void onDrawFrame() {
		// We are not animating.
		if (mAnimate == false) {
			return;
		}
		long currentTime = System.currentTimeMillis();/*밀리세컨드로 현재시간을 돌려줌*/
		// If animation is done.
		if (currentTime >= mAnimationStartTime + mAnimationDurationTime) {
			if (mAnimationTargetEvent == SET_CURL_TO_RIGHT) {
				// Switch curled page to right.
				CurlMesh right = mPageCurl;
				CurlMesh curl = mPageRight;/*매쉬의 그유명한 컬메소에 라잇이 들어가는모습*/
				right.setRect(mRenderer.getPageRect(CurlRenderer.PAGE_RIGHT));
				right.setFlipTexture(false);
				right.reset();
				mRenderer.removeCurlMesh(curl);
				mPageCurl = curl;
				mPageRight = right;
				// If we were curling left page update current index.
				if (mCurlState == CURL_LEFT) {
					//恐らく左なら-1になる
					mCurrentIndex--;
					//枚数手を出すと大変なことが起こります
					/*아마도 왼쪽을 넘어가면 --가됨 즉 -1인건가*/
				}
			} else if (mAnimationTargetEvent == SET_CURL_TO_LEFT) {
				// Switch curled page to left.
				CurlMesh left = mPageCurl;
				CurlMesh curl = mPageLeft;
				left.setRect(mRenderer.getPageRect(CurlRenderer.PAGE_LEFT));
				left.setFlipTexture(true);
				left.reset();
				mRenderer.removeCurlMesh(curl);
				if (!mRenderLeftPage) {
					mRenderer.removeCurlMesh(left);
				}
				mPageCurl = curl;
				mPageLeft = left;
				// If we were curling right page update current index.
				if (mCurlState == CURL_RIGHT) {
					//多分右なら+1になる
					mCurrentIndex++;
					/*아마도 오른쪽을 넘어가면 ++가됨 즉 1인건가 왼쪽넘어갓다 오른쪽넘기면 0이겟네*/
				}
			}
			mCurlState = CURL_NONE;
			mAnimate = false;
			requestRender();
		} else {/*이부문이 이해가,,,,*/
			mPointerPos.mPos.set(mAnimationSource);
			float t = (float) Math
					.sqrt((double) (currentTime - mAnimationStartTime)
							/ mAnimationDurationTime);
			mPointerPos.mPos.x += (mAnimationTarget.x - mAnimationSource.x) * t;
			mPointerPos.mPos.y += (mAnimationTarget.y - mAnimationSource.y) * t;
			updateCurlPos(mPointerPos);
		}
	}

	@Override
	public void onPageSizeChanged(int width, int height) {
		mPageBitmapWidth = width;
		mPageBitmapHeight = height;
		updateBitmaps();									//previewエーらの原因
		requestRender();
	}

	
	/*잘은 모르나 상위클래스  GLSurfaceView에서
	 *  int w, int h, int ow, int oh 
	 *  이러한 값을 받았다는 가정하로 계산하고 있는듯*/
	@Override
	public void onSizeChanged(int w, int h, int ow, int oh) {
		/*상위클래스에*/
		super.onSizeChanged(w, h, ow, oh);
		requestRender();
		if (mSizeChangedObserver != null) {
			mSizeChangedObserver.onSizeChanged(w, h);
		}
	}

	@Override
	public void onSurfaceCreated() {
		// In case surface is recreated, let page meshes drop allocated texture
		// ids and ask for new ones. There's no need to set textures here as
		// onPageSizeChanged should be called later on.
		mPageLeft.resetTexture();
		mPageRight.resetTexture();
		mPageCurl.resetTexture();
	}

	@Override
	public boolean onTouch(View view, MotionEvent me) {
		
		// No dragging during animation at the moment.
		// TODO: Stop animation on touch event and return to drag mode.
		if (mAnimate || mBitmapProvider == null) {
			return false;
		}

		// We need page rects quite extensively so get them for later use.
		RectF rightRect = mRenderer.getPageRect(CurlRenderer.PAGE_RIGHT);
		RectF leftRect = mRenderer.getPageRect(CurlRenderer.PAGE_LEFT);
		RectF topRect = mRenderer.getPageRect(CurlRenderer.PAGE_TOP);/*탑을 또임의로 지정하기에이름*/

		// Store pointer position.
		
		mPointerPos.mPos.set(me.getX(), me.getY());
		mRenderer.translate(mPointerPos.mPos);
		if (mEnableTouchPressure) {/*boolean =false부터 시작*/
			mPointerPos.mPressure = me.getPressure();
		} else {
			mPointerPos.mPressure = 0f;
		}

		switch (me.getAction()) {
		case MotionEvent.ACTION_DOWN: {/*이게없으면 아에 마우스 눌러도 효과가 없었당,,*/
			// Once we receive pointer down event its position is mapped to
			// right or left edge of page and that'll be the position from where
			// user is holding the paper to make curl happen.
			/*위치는에 매핑되면 우리는 이벤트 아래 포인터를받을 수
  				오른쪽 또는 왼쪽 페이지의 가장자리와 그 위치에서있을거야
  				사용자가 곱슬 곱슬 그렇게 만들수있는 종이를 들고있다.*/
			mDragStartPos.set(mPointerPos.mPos);

			// First we make sure it's not over or below page. Pages are
			// supposed to be same height so it really doesn't matter do we use
			// left or right one.
			/*먼저 우리는 페이지를 통해 또는 아래 아니라고 확신합니다. 페이지 수 있습니다
  				정말 우리가 사용하는 중요하지 않도록 같은 높이 있어야
				왼쪽이나 오른쪽으로 하나.*/
			/*아무래도 RectF와PointF의 차이를 알아야 될듯
			 *mDragStartPos.y가 rightRect.bottom보다 크다고 주니까 재밌는 현상이 나타남
			 *상단오른쪽의 페이지컬이 눈에띄게 움직이지 않음,, 
			 *그러나right,left를 설정해도 별 특징이 없이 원래의 top과 같은 이벤트컬이 일어남
			 *mDragStartPos.y를x로 변경해도 별차이가 없었음*/
			if (mDragStartPos.y > rightRect.top) {/*아무래도 RectF와PointF의 차이를 알아야 될듯*/
				mDragStartPos.y = rightRect.top;
			} 
			else if (mDragStartPos.y < rightRect.bottom) {
//				mDragStartPos.y = rightRect.bottom;/*바닥*/
			    mDragStartPos.y = rightRect.right;
			}

			// Then we have to make decisions for the user whether curl is going
			// to happen from left or right, and on which page.
			/*그렇다면 우리는 울다 지쳐가는 여부를 사용자에 대한 결정을 내릴 수있다
  				왼쪽이나 오른쪽에서 발생하고, 어떤 페이지에*/
			/*오 이거슨 두번쨰페이지 머 이딴게 아니라 뷰를 가로로 보여줬을때 처리할 수 있게 셋팅*/
			if (mViewMode == SHOW_TWO_PAGES) {
				// If we have an open book and pointer is on the left from right
				// page we'll mark drag position to left edge of left page.
				// Additionally checking mCurrentIndex is higher than zero tells
				// us there is a visible page at all.
				if (mDragStartPos.x < rightRect.left && mCurrentIndex > 0) {/*mCurrentIndex > 0 //0보다 인덱스가 크단건 오른쪽으로 넘어갓단얘기 */
					mDragStartPos.x = leftRect.left;
					startCurl(CURL_LEFT);
				}
				// Otherwise check pointer is on right page's side.
				else if (mDragStartPos.x >= rightRect.left
						&& mCurrentIndex < mBitmapProvider.getBitmapCount()) {
					mDragStartPos.x = rightRect.right;
					if (!mAllowLastPageCurl
							&& mCurrentIndex >= mBitmapProvider
									.getBitmapCount() - 1) {
						return false;
					}
					startCurl(CURL_RIGHT);
				}
			}
			/*SHOW_ONE_PAGE는 첫페이지인 경우라는 의미인듯 이걸 주석처리하니 아에 이벤트의 미동조차없었음*/
			else if (mViewMode == SHOW_ONE_PAGE) {
			float halfX = (rightRect.right + rightRect.left) / 2;
			/*그래 진짜왠지 이거인것같애*/
			/*top bottom을 쿠미아와세해서 halfX를 만들었는데 아무런 변화가 없었음 아무래도
			 * 이벤트 움직이는 것과 관련은 없어보임*/
				if (mDragStartPos.x < halfX && mCurrentIndex > 0) {/*이게 왼쪽으로 일어나기 위한 좌표값이구나*/
					mDragStartPos.x = rightRect.left;
					startCurl(CURL_LEFT);/*이게 없으면 왼쪽으로 넘어가는 이벤트가 일어나지를 않음*/
				} else if (mDragStartPos.x >= halfX
						&& mCurrentIndex < mBitmapProvider.getBitmapCount()) {
					mDragStartPos.x = rightRect.right;
					if (!mAllowLastPageCurl
							&& mCurrentIndex >= mBitmapProvider
									.getBitmapCount() - 1) {
						return false;
					}
					startCurl(CURL_RIGHT);/*이게 없으면 당연히 오른쪽으로 넘어가는 이벤트가 일어나지를 않음*/
				}
			}
			// If we have are in curl state, let this case clause flow through
			// to next one. We have pointer position and drag position defined
			// and this will create first render request given these points.
			/*우리가 곱슬 곱슬 상태에있는 경우,을 통해이 사건 조항의 흐름을 보자
  				다음 수 있습니다. 우리는 포인터의 위치와 정의 끌어 위치를
  				그리고 이것은 이러한 점을 주어진 첫번째 렌더링 요청을 생성합니다.*/
			if (mCurlState == CURL_NONE) {
				return false;
			}
		}/*아 여기까지가 손가락을 눌렀을떄의 상태*/
		case MotionEvent.ACTION_MOVE: {/*이거 안하면 페이지가 움직여지지 않음*/
			updateCurlPos(mPointerPos);
			break;
		}
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP: {/*이건,,손가락 떼면 페이지가 넘어가는데 그상황을 표현*/
			if (mCurlState == CURL_LEFT || mCurlState == CURL_RIGHT) {
				// Animation source is the point from where animation starts.
				// Also it's handled in a way we actually simulate touch events
				// meaning the output is exactly the same as if user drags the
				// page to other side. While not producing the best looking
				// result (which is easier done by altering curl position and/or
				// direction directly), this is done in a hope it made code a
				// bit more readable and easier to maintain.
				mAnimationSource.set(mPointerPos.mPos);
				mAnimationStartTime = System.currentTimeMillis();
				// Given the explanation, here we decide whether to simulate
				// drag to left or right end.
				if ((mViewMode == SHOW_ONE_PAGE && mPointerPos.mPos.x > (rightRect.left + rightRect.right) / 2)
						|| mViewMode == SHOW_TWO_PAGES
						&& mPointerPos.mPos.x > rightRect.left) {
					// On right side target is always right page's right border.
					mAnimationTarget.set(mDragStartPos);
					mAnimationTarget.x = mRenderer
							.getPageRect(CurlRenderer.PAGE_RIGHT).right;
					mAnimationTargetEvent = SET_CURL_TO_RIGHT;
				} else {
					// On left side target depends on visible pages.
					mAnimationTarget.set(mDragStartPos);
					if (mCurlState == CURL_RIGHT || mViewMode == SHOW_TWO_PAGES){
						mAnimationTarget.x = leftRect.left;
					} else {
						mAnimationTarget.x = rightRect.left;
					}
					mAnimationTargetEvent = SET_CURL_TO_LEFT;
				}
				mAnimate = true;
				requestRender();
			}
			break;
		}
		}
		Log.e("time", me.getEventTime()+toString());
		return true;
	}/*온터치 메소드 끝*/
	/**
	 * Allow the last page to curl.
	 */
	public void setAllowLastPageCurl(boolean allowLastPageCurl) {
		mAllowLastPageCurl = allowLastPageCurl;
	}
	/**
	 * Sets background color - or OpenGL clear color to be more precise. Color
	 * is a 32bit value consisting of 0xAARRGGBB and is extracted using
	 * android.graphics.Color eventually.
	 */
	 /*  배경색 컬뷰셋팅인데 얘는 여기서 또 오버라이드를 받았음 mRenderer인듯  */
	@Override
	public void setBackgroundColor(int color) {
		mRenderer.setBackgroundColor(color);
		requestRender();
	}
	/**
	 * Update/set bitmap provider.
	 */
	/* setBitmapProvider  */
	public void setBitmapProvider(BitmapProvider bitmapProvider, int num) {
		pageNum = num;						//PageNumを引き受けてNumに入れる
		mBitmapProvider = bitmapProvider;
		mCurrentIndex = 0;					//最初に0にSetting
		updateBitmaps();
		requestRender();
	}
	/**
	 * Set page index.
	 */
/* setCurrentIndex 페이지값구하는 컬뷰 */
	public void setCurrentIndex(int index) {
		if (mBitmapProvider == null || index <= 0) {
			mCurrentIndex = 0;
		} else {
			mCurrentIndex = Math.min(index,
			mBitmapProvider.getBitmapCount() - 1);
		}
		updateBitmaps();
		requestRender();
	}
	/**
	 * If set to true, touch event pressure information is used to adjust curl
	 * radius. The more you press, the flatter the curl becomes. This is
	 * somewhat experimental and results may vary significantly between devices.
	 * On emulator pressure information seems to be flat 1.0f which is maximum
	 * value and therefore not very much of use.
	 */
	public void setEnableTouchPressure(boolean enableTouchPressure) {
		mEnableTouchPressure = enableTouchPressure;
	}
	/**
	 * Set margins (or padding). Note: margins are proportional. Meaning a value
	 * of .1f will produce a 10% margin.
	 */
	public void setMargins(float left, float top, float right, float bottom) {
		mRenderer.setMargins(left, top, right, bottom);
	}
	/**
	 * Setter for whether left side page is rendered. This is useful mostly for
	 * situations where right (main) page is aligned to left side of screen and
	 * left page is not visible anyway.
	 */
	public void setRenderLeftPage(boolean renderLeftPage) {
		mRenderLeftPage = renderLeftPage;
	}
	/**
	 * Sets SizeChangedObserver for this View. Call back method is called from
	 * this View's onSizeChanged method.
	 */
	public void setSizeChangedObserver(SizeChangedObserver observer) {
		mSizeChangedObserver = observer;
	}
	/**
	 * Sets view mode. Value can be either SHOW_ONE_PAGE or SHOW_TWO_PAGES. In
	 * former case right page is made size of display, and in latter case two
	 * pages are laid on visible area.
	 */
	public void setViewMode(int viewMode) {
		//image.setVisibility(View.VISIBLE);//효과를 주려할때마다 오류다, 절망이다, 도우시떼
		switch (viewMode) {
		case SHOW_ONE_PAGE:
			mViewMode = viewMode;
			mRenderer.setViewMode(CurlRenderer.SHOW_ONE_PAGE);
			break;
		/*case SHOW_TWO_PAGES:
			mViewMode = viewMode;
			mRenderer.setViewMode(CurlRenderer.SHOW_TWO_PAGES);
			break;*/
		}
	}
	/**
	 * Initialize method.
	 */
	private void init(Context ctx) {
		mRenderer = new CurlRenderer(this);
		setRenderer(mRenderer);
		setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);/*　CurlRenderer()의 onDrawFrame()을 1회 호출.*/
		setOnTouchListener(this);
		// Even though left and right pages are static we have to allocate room
		// for curl on them too as we are switching meshes. Another way would be
		// to swap texture ids only.
		mPageLeft = new CurlMesh(10);/*숫자 조정했을떄 그림이 미세히 즈레테루 칸지가 시마스*/
		mPageRight = new CurlMesh(10);/*숫자 조정했을떄 그림이 미세히 즈레테루 칸지가 시마스*/
		mPageCurl = new CurlMesh(10);/*숫자 조정했을떄 그림이 미세히 즈레테루 칸지가 시마스*/
		mPageLeft.setFlipTexture(true);
		mPageRight.setFlipTexture(false);
	}
	/**
	 * Sets mPageCurl curl position.
	 */
	/*mPageCurl 컬 위치를 설정*/
	private void setCurlPos(PointF curlPos, PointF curlDir, double radius) {
		if (mCurlState == CURL_RIGHT
				|| (mCurlState == CURL_LEFT && mViewMode == SHOW_ONE_PAGE)) {
			/*헐 나의테스트에 의하면 얘만 불려지고  있다..ㅋ*/
			RectF pageRect = mRenderer.getPageRect(CurlRenderer.PAGE_RIGHT);
			Log.e("right desu","zz");
			if (curlPos.x >= pageRect.right) {
				mPageCurl.reset();
				requestRender();
				return;
			}
			if (curlPos.x < pageRect.left) {
				curlPos.x = pageRect.left;
			}
			if (curlDir.y != 0) {/*좀처럼 오겠는데?*/
				float diffX = curlPos.x - pageRect.left;
				Log.e("sibal jahyo diffX:",curlPos.x+"-"+pageRect.right+"="+diffX);
				float leftY = curlPos.y + (diffX * curlDir.x / curlDir.y);
				if (curlDir.y < 0 && leftY < pageRect.top) {
					curlDir.x = curlPos.y - pageRect.top;
					curlDir.y = pageRect.left - curlPos.x;
				} else if (curlDir.y > 0 && leftY > pageRect.bottom) {
					curlDir.x = pageRect.bottom - curlPos.y;
					curlDir.y = curlPos.x - pageRect.left;
				}
			}
		} else if (mCurlState == CURL_LEFT) {/*헐 나의테스트에 의하면 얘는 불려지지 않는다..ㅋ*/
			Log.e("left desu","zz");
			RectF pageRect = mRenderer.getPageRect(CurlRenderer.PAGE_LEFT);
			if (curlPos.x <= pageRect.left) {
				mPageCurl.reset();
				requestRender();
				return;
			}
			if (curlPos.x > pageRect.right) {
				curlPos.x = pageRect.right;
			}
			if (curlDir.y != 0) {
				float diffX = curlPos.x - pageRect.right;
				Log.e("sibal jahyo diffX:",curlPos.x+"-"+pageRect.right+"="+diffX);/*좀처럼 오질 않는데?*/
				float rightY = curlPos.y + (diffX * curlDir.x / curlDir.y);
				if (curlDir.y < 0 && rightY < pageRect.top) {
					curlDir.x = pageRect.top - curlPos.y;
					curlDir.y = curlPos.x - pageRect.right;
				} else if (curlDir.y > 0 && rightY > pageRect.bottom) {
					curlDir.x = curlPos.y - pageRect.bottom;
					curlDir.y = pageRect.right - curlPos.x;
				}
			}
		}
		double dist = Math.sqrt(curlDir.x * curlDir.x + curlDir.y * curlDir.y);
		if (dist != 0) {
			curlDir.x /= dist;
			curlDir.y /= dist;
			mPageCurl.curl(curlPos, curlDir, radius);
		} else {
			mPageCurl.reset();
		}

		requestRender();
	}
	/**
	 * Switches meshes and loads new bitmaps if available.
	 */
	private void startCurl(int page) {/*이게 실제 핵심일 가능성이농후*/
		switch (page) {
		// Once right side page is curled, first right page is assigned into
		// curled page. And if there are more bitmaps available new bitmap is
		// loaded into right side mesh.
		/*일단 오른쪽 페이지가 모습을 드러냅니다는 먼저 오른쪽 페이지에 할당됩니다
			페이지를 드러냅니다. 더 많은 비트맵이있다면 가능한 새 비트맵입니다
  			오른쪽 메쉬에 로드된.*/
		case CURL_RIGHT: {/*오른쪽이면*/
			// Remove meshes from renderer.
			mRenderer.removeCurlMesh(mPageLeft);
			mRenderer.removeCurlMesh(mPageRight);
			mRenderer.removeCurlMesh(mPageCurl);
			// We are curling right page.
			CurlMesh curl = mPageRight;
			mPageRight = mPageCurl;
			mPageCurl = curl;
			// If there is something to show on left page, simply add it to
			// renderer.
			if (mCurrentIndex > 0) {
				mPageLeft
						.setRect(mRenderer.getPageRect(CurlRenderer.PAGE_LEFT));
				mPageLeft.reset();
				if (mRenderLeftPage) {
					mRenderer.addCurlMesh(mPageLeft);
				}
			}
			if (mCurrentIndex < mBitmapProvider.getBitmapCount() - 1) {
				int pageNumRe = pageNum * 2;
				
				Bitmap bitmap = mBitmapProvider.getBitmap(mPageBitmapWidth,	mPageBitmapHeight,
								mCurrentIndex + pageNumRe); //8);//ぺーじ数を決定//最初のページだけ
				Log.e("pageNumgogogogogogoggogogogogogo","GOGOGO"+pageNum+"");
				mPageRight.setBitmap(bitmap);
				bitmap.recycle();
				bitmap = null;
				mPageRight.setRect(mRenderer.getPageRect(CurlRenderer.PAGE_RIGHT));
				mPageRight.setFlipTexture(false);
				mPageRight.reset();
				mRenderer.addCurlMesh(mPageRight);
			}
			// Add curled page to renderer.
			mPageCurl.setRect(mRenderer.getPageRect(CurlRenderer.PAGE_RIGHT));
			mPageCurl.setFlipTexture(false);
			mPageCurl.reset();
			mRenderer.addCurlMesh(mPageCurl);
			mCurlState = CURL_RIGHT;
			break;
		}
			// On left side curl, left page is assigned to curled page. And if
			// there are more bitmaps available before currentIndex, new bitmap
			// is loaded into left page.
		case CURL_LEFT: {/*왼쪽이면*/
			// Remove meshes from renderer.
			mRenderer.removeCurlMesh(mPageLeft);
			mRenderer.removeCurlMesh(mPageRight);
			mRenderer.removeCurlMesh(mPageCurl);
			// We are curling left page.
			CurlMesh curl = mPageLeft;
			mPageLeft = mPageCurl;
			mPageCurl = curl;
	
			// If there is new/previous bitmap available load it to left page.
			if (mCurrentIndex > 1) {
				Bitmap bitmap = mBitmapProvider.getBitmap(mPageBitmapWidth,
						mPageBitmapHeight, mCurrentIndex - 2);
				mPageLeft.setBitmap(bitmap);
				bitmap.recycle();
				bitmap = null;
				mPageLeft
						.setRect(mRenderer.getPageRect(CurlRenderer.PAGE_LEFT));
				mPageLeft.setFlipTexture(true);
				mPageLeft.reset();
				if (mRenderLeftPage) {
					mRenderer.addCurlMesh(mPageLeft);
				}
			}
			// If there is something to show on right page add it to renderer.
			if (mCurrentIndex < mBitmapProvider.getBitmapCount()) {
				mPageRight.setRect(mRenderer
						.getPageRect(CurlRenderer.PAGE_RIGHT));
				mPageRight.reset();
				mRenderer.addCurlMesh(mPageRight);
			}			
			// How dragging previous page happens depends on view mode.
			if (mViewMode == SHOW_ONE_PAGE) {
				mPageCurl.setRect(mRenderer
						.getPageRect(CurlRenderer.PAGE_RIGHT));
				mPageCurl.setFlipTexture(false);
			} else {
				mPageCurl
						.setRect(mRenderer.getPageRect(CurlRenderer.PAGE_LEFT));
				mPageCurl.setFlipTexture(true);
			}
			mPageCurl.reset();
			mRenderer.addCurlMesh(mPageCurl);

			mCurlState = CURL_LEFT;
			break;
		}
		}
	}/*startCurl메소드 닫기*/
	
	/**
	 * Updates bitmaps for left and right meshes.
	 */
	//確信メソットPage両面を調節
	private void updateBitmaps() {
		if (mBitmapProvider == null || mPageBitmapWidth <= 0
				|| mPageBitmapHeight <= 0) {
			return;
		}
		// Remove meshes from renderer.
		mRenderer.removeCurlMesh(mPageLeft);
		mRenderer.removeCurlMesh(mPageRight);
		mRenderer.removeCurlMesh(mPageCurl);
		//ここからPageIndexを計算する部分
		//どうすればいいのか考えろ、きっと手があるはず
		int leftIdx = mCurrentIndex - 1;
		int rightIdx = mCurrentIndex;
		int curlIdx = -1;
		if (mCurlState == CURL_LEFT) {
			curlIdx = leftIdx;
			leftIdx--;
		} else if (mCurlState == CURL_RIGHT) {
			curlIdx = rightIdx;
			//rightIdx = rightIdx + 2;//
			rightIdx++;
		}
		if (rightIdx >= 0 && rightIdx < mBitmapProvider.getBitmapCount()) {
			Bitmap bitmap = mBitmapProvider.getBitmap(mPageBitmapWidth,
					mPageBitmapHeight, rightIdx);
			mPageRight.setBitmap(bitmap); //previewエーらの原因
			bitmap.recycle();
			bitmap = null;
			mPageRight.setRect(mRenderer.getPageRect(CurlRenderer.PAGE_RIGHT));
			mPageRight.reset();
			mRenderer.addCurlMesh(mPageRight);
		}
		if (leftIdx >= 0 && leftIdx < mBitmapProvider.getBitmapCount()) {
			Bitmap bitmap = mBitmapProvider.getBitmap(mPageBitmapWidth,
					mPageBitmapHeight, leftIdx);
			mPageLeft.setBitmap(bitmap);
			bitmap.recycle();
			bitmap = null;
			mPageLeft.setRect(mRenderer.getPageRect(CurlRenderer.PAGE_LEFT));
			mPageLeft.reset();
			if (mRenderLeftPage) {
				mRenderer.addCurlMesh(mPageLeft);
			}
		}
		if (curlIdx >= 0 && curlIdx < mBitmapProvider.getBitmapCount()) {
			Bitmap bitmap = mBitmapProvider.getBitmap(mPageBitmapWidth,
					mPageBitmapHeight, curlIdx);
			mPageCurl.setBitmap(bitmap);
			bitmap.recycle();
			bitmap = null;
			if (mCurlState == CURL_RIGHT
					|| (mCurlState == CURL_LEFT && mViewMode == SHOW_TWO_PAGES)) {
				mPageCurl.setRect(mRenderer
						.getPageRect(CurlRenderer.PAGE_RIGHT));
			} else {
				mPageCurl
						.setRect(mRenderer.getPageRect(CurlRenderer.PAGE_LEFT));
			}
			mPageCurl.reset();
			mRenderer.addCurlMesh(mPageCurl);
		}
	}
	/**
	* Updates curl position.
	*/
	private void updateCurlPos(PointerPosition pointerPos) {
		// Default curl radius.
		double radius = mRenderer.getPageRect(CURL_RIGHT).width() / 3;
		// TODO: This is not an optimal solution. Based on feedback received so
		// far; pressure is not very accurate, it may be better not to map
		// coefficient to range [0f, 1f] but something like [.2f, 1f] instead.
		// Leaving it as is until get my hands on a real device. On emulator
		// this doesn't work anyway.
		radius *= Math.max(1f - pointerPos.mPressure, 0f);
		// NOTE: Here we set pointerPos to mCurlPos. It might be a bit confusing
		// later to see e.g "mCurlPos.x - mDragStartPos.x" used. But it's
		// actually pointerPos we are doing calculations against. Why? Simply to
		// optimize code a bit with the cost of making it unreadable. Otherwise
		// we had to this in both of the next if-else branches.
		mCurlPos.set(pointerPos.mPos);
		// If curl happens on right page, or on left page on two page mode,
		// we'll calculate curl position from pointerPos.
		if (mCurlState == CURL_RIGHT	|| (mCurlState == CURL_LEFT && mViewMode == SHOW_TWO_PAGES)) {
			mCurlDir.x = mCurlPos.x - mDragStartPos.x;
			mCurlDir.y = mCurlPos.y - mDragStartPos.y;
			float dist = (float) Math.sqrt(mCurlDir.x * mCurlDir.x + mCurlDir.y	* mCurlDir.y);
			// Adjust curl radius so that if page is dragged far enough on
			// opposite side, radius gets closer to zero.
			float pageWidth = mRenderer.getPageRect(CurlRenderer.PAGE_RIGHT).width();
			double curlLen = radius * Math.PI;
			if (dist > (pageWidth * 2) - curlLen) {
				curlLen = Math.max((pageWidth * 2) - dist, 0f);
				radius = curlLen / Math.PI;
			}
			// Actual curl position calculation.
			if (dist >= curlLen) {
				double translate = (dist - curlLen) / 2;
				mCurlPos.x -= mCurlDir.x * translate / dist;
				mCurlPos.y -= mCurlDir.y * translate / dist;
			} else {
				double angle = Math.PI * Math.sqrt(dist / curlLen);
				double translate = radius * Math.sin(angle);
				mCurlPos.x += mCurlDir.x * translate / dist;
				mCurlPos.y += mCurlDir.y * translate / dist;
			}
			setCurlPos(mCurlPos, mCurlDir, radius);
		}
		// Otherwise we'll let curl follow pointer position.
		else if (mCurlState == CURL_LEFT){
			// Adjust radius regarding how close to page edge we are.
			float pageLeftX = mRenderer.getPageRect(CurlRenderer.PAGE_RIGHT).left;
			radius = Math.max(Math.min(mCurlPos.x - pageLeftX, radius), 0f);
			float pageRightX = mRenderer.getPageRect(CurlRenderer.PAGE_RIGHT).right;
			mCurlPos.x -= Math.min(pageRightX - mCurlPos.x, radius);
			mCurlDir.x = mCurlPos.x + mDragStartPos.x;
			mCurlDir.y = mCurlPos.y - mDragStartPos.y;
			setCurlPos(mCurlPos, mCurlDir, radius);
		}
	}
	/**
	 * Provider for feeding 'book' with bitmaps which are used for rendering
	 * pages.
	 */
	/*  CurlActivity에 반드시 실행해야될 시발새끼들*/
	//
	public interface BitmapProvider {
		/**
		 * Called once new bitmap is needed. Width and height are in pixels
		 * telling the size it will be drawn on screen and following them
		 * ensures that aspect ratio remains. But it's possible to return bitmap
		 * of any size though.<br/>
		 * <br/>
		 * Index is a number between 0 and getBitmapCount() - 1.
		 */
		public Bitmap getBitmap(int width, int height, int index);
		/**
		 * Return number of pages/bitmaps available.
		 */
		public int getBitmapCount();
	}
	/**
	 * Observer interface for handling CurlView size changes.
	 */
	/*  CurlActivity에 반드시 실행해야될 시발새끼들*/
	public interface SizeChangedObserver {
		/**
		 * Called once CurlView size changes.
		 */
		public void onSizeChanged(int width, int height);
	}
	/**
	 * Simple holder for pointer position.
	 */
	private class PointerPosition {
		PointF mPos = new PointF();
		float mPressure;
	}
	public void onLongClick(OnLongClickListener onLongClickListener) {
		// TODO Auto-generated method stub
		Log.e("dddddddddddddddd","asdaa");
	}
	@Override
	public boolean onLongClick(View view) {
		// TODO Auto-generated method stub
		Log.e("zzzzzzzzzzzzzzzzz","asdaa");
		return false;/*7.14일단 세화는 여기를 트루로 바꾸도록한다*/
	}
}

