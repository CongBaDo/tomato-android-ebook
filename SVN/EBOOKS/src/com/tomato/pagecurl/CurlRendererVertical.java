package com.tomato.pagecurl;

import java.util.Vector;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.RectF;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;

/**
 * Actual renderer class.
 * 
 * @author harism
 */
public class CurlRendererVertical implements GLSurfaceView.Renderer {

	// Constants for changing view mode.
	public static final int SHOW_ONE_PAGE = 1;
	public static final int SHOW_TWO_PAGES = 2;
	private int mViewMode = SHOW_ONE_PAGE;

	// Constant for requesting left page rect.
	public static final int PAGE_LEFT = 1;
	// Constant for requesting right page rect.
	public static final int PAGE_RIGHT = 2;
	public static final int PAGE_TOP = 3;/*렌더까지 탑을  셋팅*/

	// Set to true for checking quickly how perspective projection looks.
	private static final boolean USE_PERSPECTIVE_PROJECTION = false;

	// Rect for render area.
	private RectF mViewRect = new RectF();
	private RectF mMargins = new RectF();
	// Screen size.
	private int mViewportWidth;/*뭐하는 친군지 강제로 값을 바꿔보도록하자 아안해도 되겠다 이거 전체크기*/
	private int mViewportHeight;

	// Curl meshes used for static and dynamic rendering.
	private Vector<CurlMeshVertical> mCurlMeshes;

	private boolean mBackgroundColorChanged = false;
	private int mBackgroundColor;

	private CurlRendererVertical.Observer mObserver;

	private RectF mPageRectLeft;
	private RectF mPageRectRight;
	private RectF mPageRectTop;/*네모그리기도 탑은 실행되야하기에 셋팅*/

	/**
	 * Basic constructor.
	 */
	public CurlRendererVertical(CurlRendererVertical.Observer observer) {
		mObserver = observer;
		mCurlMeshes = new Vector<CurlMeshVertical>();
		mPageRectLeft = new RectF();
		mPageRectRight = new RectF();
		mPageRectTop = new RectF();
	}

	/**
	 * Adds CurlMesh to this renderer.
	 */
	public synchronized void addCurlMesh(CurlMeshVertical mesh) {
		removeCurlMesh(mesh);
		mCurlMeshes.add(mesh);
	}

	/**
	 * Returns rect reserved for left or right page. Value page should be
	 * PAGE_LEFT or PAGE_RIGHT.
	 */
	/*왼쪽이나 오른쪽 페이지에 대한 rect 소유를 반환합니다. 값 페이지되어야합니다
  PAGE_LEFT 또는 PAGE_RIGHT.*/
	public RectF getPageRect(int page) {/*getPageRect(CurlRenderer.PAGE_LEFT);이런식으로 부르긴
	하더만 int page랑 비교하게 되네 그리고 PAGE_LEFT또는 PAGE_RIGHT로 비교하는구나*/
		if (page == PAGE_LEFT) {
			return mPageRectLeft;
		} else if (page == PAGE_RIGHT) {
			return mPageRectRight;
		} else if(page == PAGE_TOP){/*세화는 페이지가 먼지도 모르고 탑을 비교하기에 이른다*/
			return mPageRectTop;
		}
		return null;
	}

	@Override
	public synchronized void onDrawFrame(GL10 gl) {

		mObserver.onDrawFrame();

		if (mBackgroundColorChanged) {//배경색을 결정함
			gl.glClearColor(Color.red(mBackgroundColor) / 255f,
					Color.green(mBackgroundColor) / 255f,
					Color.blue(mBackgroundColor) / 255f,
					Color.alpha(mBackgroundColor) / 255f);
			mBackgroundColorChanged = false;
		}
		/*밑에gl.glClear(GL10.GL_COLOR_BUFFER_BIT);문을 주석하면 흥미로운 현상이!!그림이 아주 엉망이됨 심각하게 허나 이벤트는 돌아감
		 * drawable-gensyou.png를 참고>_<*/
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT); // | GL10.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();/*행렬처리가 이루어 지기 전에 좌표계를 초기화 하는 역활.*/




		if (USE_PERSPECTIVE_PROJECTION) {
			gl.glTranslatef(0, 0, -6f);
		}

/*밑에 있는 포문을 지우면 화면이 뜨지를 않으니 주의!!*/		
		for (int i = 0; i < mCurlMeshes.size(); ++i) {
			mCurlMeshes.get(i).draw(gl);/*벡터사이즈 만큼 그린다는게 아닐까?*/
		}
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		gl.glViewport(0, 0, width, height);/*여기를 수정하면 크기가변햇음*/
		mViewportWidth = width;/*여기를 수정하면 크기가변햇음*/
		mViewportHeight = height;/*여기를 수정하면 크기가변햇음*/

		float ratio = (float) width / height;
		mViewRect.top = 1.0f;
		mViewRect.bottom = -1.0f;
		mViewRect.left = -ratio;
		mViewRect.right = ratio;
		updatePageRects();

		gl.glMatrixMode(GL10.GL_PROJECTION);/*투영을 위한 행렬 변환을 하는 부분.*/
		gl.glLoadIdentity();/*행렬처리가 이루어 지기 전에 좌표계를 초기화 하는 역활.*/
		if (USE_PERSPECTIVE_PROJECTION) {
			GLU.gluPerspective(gl, 20f, (float) width / height, .1f, 100f);
		} else {
			/*이라인이 없으면 사진이 좀 좁아지는 효과 정도?*/
			GLU.gluOrtho2D(gl, mViewRect.left, mViewRect.right,
					mViewRect.bottom, mViewRect.top);
		}

		gl.glMatrixMode(GL10.GL_MODELVIEW);/*투영을 위한 행렬 변환을 하는 부분.*/
		gl.glLoadIdentity();/*행렬처리가 이루어 지기 전에 좌표계를 초기화 하는 역활.*/
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		/*gl라인은 주석처리해도 뭐 이벤트와 관계없이 돌아가네?오카시이나 ㅠㅠ*/
		gl.glClearColor(0f, 0f, 0f, 1f);
		gl.glShadeModel(GL10.GL_SMOOTH);
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
		gl.glHint(GL10.GL_LINE_SMOOTH_HINT, GL10.GL_NICEST);
		gl.glHint(GL10.GL_POLYGON_SMOOTH_HINT, GL10.GL_NICEST);
		gl.glEnable(GL10.GL_LINE_SMOOTH);
		gl.glDisable(GL10.GL_DEPTH_TEST);
		gl.glDisable(GL10.GL_CULL_FACE);

		mObserver.onSurfaceCreated();/*이쪽도 주석해도 이벤트와는 관계가,,*/
	}

	/**
	 * Removes CurlMesh from this renderer.
	 */
	/*이 렌더 러에서 CurlMesh를 제거합니다.*/
	public synchronized void removeCurlMesh(CurlMeshVertical mesh) {
		while (mCurlMeshes.remove(mesh))
			;
	}

	/**
	 * Change background/clear color.
	 */
	public void setBackgroundColor(int color) {
		mBackgroundColor = color;
		mBackgroundColorChanged = true;
	}

	/**
	 * Set margins or padding. Note: margins are proportional. Meaning a value
	 * of .1f will produce a 10% margin.
	 */
	/*마진이나 패딩을 설정합니다. 참고 : 여백은 비례합니다. 0.1 F의 값을 의미하는 것은 10 %의 마진을 생산합니다.*/
	public synchronized void setMargins(float left, float top, float right,
			float bottom) {
		mMargins.left = left;
		mMargins.top = top;
		mMargins.right = right;
		mMargins.bottom = bottom;
		updatePageRects();
	}

	/**
	 * Sets visible page count to one or two. Should be either SHOW_ONE_PAGE or
	 * SHOW_TWO_PAGES.
	 */
	public synchronized void setViewMode(int viewmode) {
		if (viewmode == SHOW_ONE_PAGE) {
			mViewMode = viewmode;
			updatePageRects();
		}// else if (viewmode == SHOW_TWO_PAGES) {//주석처리와 동시에 가로도 세로와 동일하게 나타남
		//	mViewMode = viewmode;
		//	updatePageRects();
		//}
	}

	/**
	 * Translates screen coordinates into view coordinates.
	 */
	public void translate(PointF pt) {
		pt.x = mViewRect.left + (mViewRect.width() * pt.x / mViewportWidth);
		pt.y = mViewRect.top - (-mViewRect.height() * pt.y / mViewportHeight);
	}

	/**
	 * Recalculates page rectangles.
	 */
	private void updatePageRects() {
		if (mViewRect.width() == 0 || mViewRect.height() == 0){
			return;
		} else if (mViewMode == SHOW_ONE_PAGE) {
			mPageRectRight.set(mViewRect);
			mPageRectRight.left += mViewRect.width() * mMargins.left;
			mPageRectRight.right -= mViewRect.width() * mMargins.right;
			mPageRectRight.top += mViewRect.height() * mMargins.top;
			mPageRectRight.bottom -= mViewRect.height() * mMargins.bottom;
			/* 곱하기mMargins을 주석처리하니까 아에 실행이안되네 뭔가 밀접한 관련이 있을지도*/
//			mPageRectRight.left += mViewRect.width();
//			mPageRectRight.right -= mViewRect.width();
//			mPageRectRight.top += mViewRect.height() ;
//			mPageRectRight.bottom -= mViewRect.height();

			mPageRectLeft.set(mPageRectRight);
			mPageRectLeft.offset(-mPageRectRight.width(), 2);/*이것이 진정한 핵심 숫자 0을 2로바꾸니 왼쪽페이지가 왼쪽 상단으로 사라짐
			 													급한대로 일단 이렇게 하기로한다*/

			int bitmapW = (int) ((mPageRectRight.width() * mViewportWidth) / mViewRect	//landscape상태일때를 결정
					.width());
			int bitmapH = (int) ((mPageRectRight.height() * mViewportHeight) / mViewRect	//수정결과 실행이상화면문제
					.height());
			mObserver.onPageSizeChanged(bitmapW, bitmapH);
			
		} 
		else if (mViewMode == SHOW_TWO_PAGES) {/*쇼투페이지는 일단 재끼자 가로니까*/
			mPageRectRight.set(mViewRect);
			mPageRectRight.left += mViewRect.width() * mMargins.left;
			mPageRectRight.right -= mViewRect.width() * mMargins.right;
			mPageRectRight.top += mViewRect.height() * mMargins.top;
			mPageRectRight.bottom -= mViewRect.height() * mMargins.bottom;

			mPageRectLeft.set(mPageRectRight);
			mPageRectLeft.right = (mPageRectLeft.right + mPageRectLeft.left) / 2;
			mPageRectRight.left = mPageRectLeft.right;

			int bitmapW = (int) ((mPageRectRight.width() * mViewportWidth) / mViewRect
					.width());
			int bitmapH = (int) ((mPageRectRight.height() * mViewportHeight) / mViewRect
					.height());
			mObserver.onPageSizeChanged(bitmapW, bitmapH);
		}
	}//주석처리를 해도 별이상이 없다

	/**
	 * Observer for waiting render engine/state updates.
	 */
	public interface Observer {
		/**
		 * Called from onDrawFrame called before rendering is started. This is
		 * intended to be used for animation purposes.
		 * 렌더링이 시작되기 전에 onDrawFrame에서 호출했다. 이것은 애니메이션 목적으로 사용하기위한 것입니다.
		 */
		public void onDrawFrame();

		/**
		 * Called once page size is changed. Width and height tell the page size
		 * in pixels making it possible to update textures accordingly.
		 * 페이지 크기가 변경되면 호출됩니다. 너비와 높이가 가능한 따라 텍스처를 업데이 트하는 픽셀의 페이지 크기를 말해.
		 */
		public void onPageSizeChanged(int width, int height);

		/**
		 * Called from onSurfaceCreated to enable texture re-initialization etc
		 * what needs to be done when this happens.
		 * 이와 같은 일이 발생하면해야할 일들을 텍스처를 다시 초기화 등을 활성화 onSurfaceCreated에서 호출됩니다.
		 */
		public void onSurfaceCreated();
	}
}
