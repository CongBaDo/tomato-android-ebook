package com.tomato.pagecurl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.RectF;
import android.opengl.GLUtils;

/**
 * Class implementing actual curl/page rendering.
 * 
 * @author harism
 */
public class CurlMesh {

	// Flag for rendering some lines used for developing. Shows
	// curl position and one for the direction from the
	// position given. Comes handy once playing around with different
	// ways for following pointer.
	/*개발에 사용된 몇 가지 라인을 렌더링 플래그. 쇼
의 방향에 대한 위치와 한 컬
  위치는 주어진. 유용 다음과 같은 포인터에 대해 서로 다른 방법으로 장난 한 번.*/
	private static final boolean DRAW_CURL_POSITION = false;
	// Flag for drawing polygon outlines. Using this flag crashes on emulator
	// due to reason unknown to me. Leaving it here anyway as seeing polygon
	// outlines gives good insight how original rectangle is divided.
	/*다각형의 윤곽선을 그리는 플래그. 이 플래그를 사용하면 에뮬레이터에서 충돌
  나에게 알 수없는 이유로 인해. 다각형을 보는 등 어쨌든 이곳을 떠나
  윤곽선은 원래 사각형을 분할하는 방법 좋은 통찰력을 제공합니다.*/
	private static final boolean DRAW_POLYGON_OUTLINES = false;
	// Flag for texture rendering. While this is likely something you
	// don't want to do it's been used for development purposes as texture
	// rendering is rather slow on emulator.
	/*텍스쳐 렌더링을위한 플래그. 이것이 당신이하고 싶지 않아 가능성이 뭔가있는 동안
	 *그것은 텍스쳐로 개발 목적으로 사용되고있어
                렌더링 에뮬레이터에서 상당히 느립니다.*/
	private static final boolean DRAW_TEXTURE = true;
	// Flag for enabling shadow rendering.
	/*그림자 렌더링 활성화 플래그.*/
	private static final boolean DRAW_SHADOW = true;

	// Colors for shadow. Inner one is the color drawn next to surface where
	// shadowed area starts and outer one is color shadow ends to.
	/*그림자에 대한 색상. 내부 하나는 어디 표면 옆에 그려진 색깔
  그림자 영역 시작하고 외부 한 색상 그림자가 종료됩니다.*/
	private static final float[] SHADOW_INNER_COLOR = { 0f, 0f, 0f, .5f };
	private static final float[] SHADOW_OUTER_COLOR = { 0f, 0f, 0f, .0f };

	// Alpha values for front and back facing texture.
	/*앞면과 뒷면 텍스쳐에 직면에 대한 알파 값.*/
	private static final double BACKFACE_ALPHA = .2f;
	private static final double FRONTFACE_ALPHA = 1f;
	// Boolean for 'flipping' texture sideways.
	/*텍스처 측면 '을 내리고'를 부울.*/
	private boolean mFlipTexture = false;

	// For testing purposes.
	/*테스트 목적으로.*/
	private int mCurlPositionLinesCount;
	private FloatBuffer mCurlPositionLines;

	// Buffers for feeding rasterizer.
	/*rasterizer 먹이를위한 버퍼.*/
	private FloatBuffer mVertices;
	private FloatBuffer mTexCoords;
	private FloatBuffer mColors;
	private int mVerticesCountFront;
	private int mVerticesCountBack;

	private FloatBuffer mShadowColors;
	private FloatBuffer mShadowVertices;
	private int mDropShadowCount;
	private int mSelfShadowCount;

	// Maximum number of split lines used for creating a curl.
	/*곱슬 곱슬를 만드는 데 사용 분할 라인의 최대 수입니다.*/
	private int mMaxCurlSplits;

	// Bounding rectangle for this mesh. mRectagle[0] = top-left corner,
	// mRectangle[1] = bottom-left, mRectangle[2] = top-right and mRectangle[3]
	// bottom-right.
	/*이 메쉬에 대한 사각형 경계. mRectagle [0] = 왼쪽 상단, mRectangle [1] = 아래 - 왼쪽,
	 *  mRectangle [2] = 오른쪽 상단과 mRectangle [3] 오른쪽 하단.*/
	private Vertex[] mRectangle = new Vertex[4];

	// One and only texture id.
	/*유일한 텍스처 ID입니다.*/
	private int[] mTextureIds = null;
	private Bitmap mBitmap = null;
	private RectF mTextureRect = new RectF();

	// Let's avoid using 'new' as much as possible. Meaning we introduce arrays
	// once here and reuse them on runtime. Doesn't really have very much effect
	// but avoids some garbage collections from happening.
	/*'새'최대한 사용하지 않도록하자. 우리가 배열을 소개 의미
  한번 여기 런타임에 그들을 재사 용할 수. 정말 아주 많이 영향을 미치지 않습니다
  하지만 무슨 일이의 일부 가비지 컬렉션을 방지합니다.*/
	private Array<Vertex> mTempVertices;/*정점*/
	private Array<Vertex> mIntersections;/*교차로*/
	private Array<Vertex> mOutputVertices;/*바깥쪽 정점?*/
	private Array<Vertex> mRotatedVertices;/*회전 정점?*/
	private Array<Double> mScanLines;/*스캔줄?*/
	private Array<ShadowVertex> mTempShadowVertices;/*그림자 정점*/
	private Array<ShadowVertex> mSelfShadowVertices;/*셀프 그림자 정점*/
	private Array<ShadowVertex> mDropShadowVertices;/*떨어지는 그림자 정점*/

	/**
	 * Constructor for mesh object.
	 * 
	 * @param maxCurlSplits
	 *            Maximum number curl can be divided into. The bigger the value
	 *            the smoother curl will be. With the cost of having more
	 *            polygons for drawing.
	 */
	/*최대의 곱슬 곱슬이로 나눌 수 있습니다.큰 값을 부드러운 컬 될 것이다.
	 *  드로잉에 대한 더 다각형를 갖는 비용.*/
	public CurlMesh(int maxCurlSplits) {
		// There really is no use for 0 splits.
		mMaxCurlSplits = maxCurlSplits < 1 ? 1 : maxCurlSplits;/*이거 루프포문인건가*/

		mScanLines = new Array<Double>(maxCurlSplits + 2);
		mOutputVertices = new Array<Vertex>(7);
		mRotatedVertices = new Array<Vertex>(4);
		mIntersections = new Array<Vertex>(2);
		/*7+4를 17+4로 해봤는데 큰변화가 없었음 포문도 같이햇고*/
		mTempVertices = new Array<Vertex>(7 + 4);
		for (int i = 0; i < 7 + 4; ++i) {
			mTempVertices.add(new Vertex());/*add를 여기서 부르네염*/
		}

		/*DRAW_SHADOW를 주석처리하니까 마우스로 드레그할때 에러가남*/
		/*2*2를 7로 바꿨는데 별다른 에러가없었음 아무래도 그림자쪽관련이라 티가 안나는듯*/
		if (DRAW_SHADOW) {
			mSelfShadowVertices = new Array<ShadowVertex>(
					(mMaxCurlSplits + 2) * 2);
			mDropShadowVertices = new Array<ShadowVertex>(
					(mMaxCurlSplits + 2) * 2);
			mTempShadowVertices = new Array<ShadowVertex>(
					(mMaxCurlSplits + 2) * 2);
			for (int i = 0; i < (mMaxCurlSplits + 2) * 2; ++i) {
				mTempShadowVertices.add(new ShadowVertex());
			}
		}

		// Rectangle consists of 4 vertices. Index 0 = top-left, index 1 =
		// bottom-left, index 2 = top-right and index 3 = bottom-right.
		/*직사각형은 4 정점으로 구성되어 있습니다. 인덱스 0 = 왼쪽 상단, 색인 1 =
                         하단 왼쪽, 색인 2 = 오른쪽 상단 및 색인 3 = 오른쪽 하단.*/
		for (int i = 0; i < 4; ++i) {
			mRectangle[i] = new Vertex();
		}
		// Set up shadow penumbra direction to each vertex. We do fake 'self
		// shadow' calculations based on this information.
		/*각 정점에 그림자 penumbra 방향을 설정합니다. 
		 * 우리는이 정보를 기반으로 '자기 그림자'계산을 가짜 않습니다.*/
		mRectangle[0].mPenumbraX = mRectangle[1].mPenumbraX = mRectangle[1].mPenumbraY = mRectangle[3].mPenumbraY = -1;
		mRectangle[0].mPenumbraY = mRectangle[2].mPenumbraX = mRectangle[2].mPenumbraY = mRectangle[3].mPenumbraX = 1;

		if (DRAW_CURL_POSITION) {
			/*mCurlPositionLinesCount=103으로 바꿨는데도 별다른 변화가없었음*/
			mCurlPositionLinesCount = 3;
//			mCurlPositionLinesCount = 103;
			
			ByteBuffer hvbb = ByteBuffer
			/*밑에 주석과 같이 과격하게 200*200*40으로도 바꿔보았는데 변화가 없었음*/
//			.allocateDirect(mCurlPositionLinesCount * 200 * 200 * 40);
								.allocateDirect(mCurlPositionLinesCount * 2 * 2 * 4);
			hvbb.order(ByteOrder.nativeOrder());/*이걸 주석해도 별다른 변화가 없네*/
			mCurlPositionLines = hvbb.asFloatBuffer();/*이것도,,*/
			mCurlPositionLines.position(0);/*이것도,,*/
		}

		// There are 4 vertices from bounding rect, max 2 from adding split line
		// to two corners and curl consists of max mMaxCurlSplits lines each
		// outputting 2 vertices.
		/*4 정점은 최대로 구성되어 두 코너로 분할 라인을 추가하고 곱슬 곱슬에서 rect,
		 *  최대 2 경계에서 선이 있습니다 각 출력이 정점을 mMaxCurlSplits.*/
		int maxVerticesCount = 4 + 2 + (2 * mMaxCurlSplits);
		/*밑에 주석문으로 변경해봤으나 별이벤트의 변화가 없었음*/
//		ByteBuffer vbb = ByteBuffer.allocateDirect(maxVerticesCount * 300 * 40);
		ByteBuffer vbb = ByteBuffer.allocateDirect(maxVerticesCount * 3 * 4);
		vbb.order(ByteOrder.nativeOrder());
		mVertices = vbb.asFloatBuffer();
		mVertices.position(0);

		if (DRAW_TEXTURE) {
			ByteBuffer tbb = ByteBuffer
			/*이쪽도 2*4를 200*40으로 바꿔봤지만,,,서도,,ㅠㅠ*/
					.allocateDirect(maxVerticesCount * 2 * 4);
			tbb.order(ByteOrder.nativeOrder());
			mTexCoords = tbb.asFloatBuffer();
			mTexCoords.position(0);
		}
/*밑에주석을 400*40으로 해봤지만 별다른,,*/
		ByteBuffer cbb = ByteBuffer.allocateDirect(maxVerticesCount * 4 * 4);
		cbb.order(ByteOrder.nativeOrder());
		mColors = cbb.asFloatBuffer();
		mColors.position(0);

		if (DRAW_SHADOW) {
			int maxShadowVerticesCount = (mMaxCurlSplits + 2) * 2 * 2;
			ByteBuffer scbb = ByteBuffer
					.allocateDirect(maxShadowVerticesCount * 4 * 4);
			scbb.order(ByteOrder.nativeOrder());
			mShadowColors = scbb.asFloatBuffer();
			mShadowColors.position(0);

			ByteBuffer sibb = ByteBuffer
					.allocateDirect(maxShadowVerticesCount * 3 * 4);
			sibb.order(ByteOrder.nativeOrder());
			mShadowVertices = sibb.asFloatBuffer();
			mShadowVertices.position(0);

			mDropShadowCount = mSelfShadowCount = 0;
		}
	}

	/**
	 * Sets curl for this mesh.
	 * 
	 * @param curlPos
	 *            Position for curl 'center'. Can be any point on line collinear
	 *            to curl.
	 * @param curlDir
	 *            Curl direction, should be normalized.
	 * @param radius
	 *            Radius of curl.
	 */
	/*ㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁ*/
	public synchronized void curl(PointF curlPos, PointF curlDir, double radius) {

		// First add some 'helper' lines used for development.
		/*먼저 개발에 사용되는 '도우미'라인을 추가합니다.*/
		if (DRAW_CURL_POSITION) {
			mCurlPositionLines.position(0);
            /*mCurlPositionLines.put을 전부 주석해도 이벤트와 문제는 없었음*/
			mCurlPositionLines.put(curlPos.x);
			mCurlPositionLines.put(curlPos.y - 1.0f);
			mCurlPositionLines.put(curlPos.x);
			mCurlPositionLines.put(curlPos.y + 1.0f);
			mCurlPositionLines.put(curlPos.x - 1.0f);
			mCurlPositionLines.put(curlPos.y);
			mCurlPositionLines.put(curlPos.x + 1.0f);
			mCurlPositionLines.put(curlPos.y);

			mCurlPositionLines.put(curlPos.x);
			mCurlPositionLines.put(curlPos.y);
			mCurlPositionLines.put(curlPos.x + curlDir.x * 2);
			mCurlPositionLines.put(curlPos.y + curlDir.y * 2);

			mCurlPositionLines.position(0);
		}

		// Actual 'curl' implementation starts here.
		mVertices.position(0);
		mColors.position(0);
		if (DRAW_TEXTURE) {
			mTexCoords.position(0);
		}

		// Calculate curl angle from direction.
		/*방향에서 각도 말아 계산.*/
		/*밑에꺼 짦아도 주석질하면  왼쪽으로만 페이지 넘어가는 이벤트가 발생됨*/
		double curlAngle = Math.acos(curlDir.x);/*아크코사인 함수로 바꿔준다고는하더만*/	
		curlAngle = curlDir.y > 0 ? -curlAngle : curlAngle;
		
		// Initiate rotated rectangle which's is translated to curlPos and
		// rotated so that curl direction heads to right (1,0). Vertices are
		// ordered in ascending order based on x -coordinate at the same time.
		// And using y -coordinate in very rare case in which two vertices have
		// same x -coordinate.
		/*curlPos로 번역하고있다의 회전 사각형을 개시
회전되는 컬 방향은 오른쪽 (1,0)으로 머리 때문에. 정점은
  동시에 X 좌표에 따라 오름차순으로 명령했다.
  두 정점이 보유하고있는 매우 드문 경우에 Y 좌표를 사용하여
같은 X 좌표.*/
		mTempVertices.addAll(mRotatedVertices);
		mRotatedVertices.clear();
		for (int i = 0; i < 4; ++i) {
			Vertex v = mTempVertices.remove(0);
			v.set(mRectangle[i]);
			v.translate(-curlPos.x, -curlPos.y);
			v.rotateZ(-curlAngle);
			int j = 0;
			for (; j < mRotatedVertices.size(); ++j) {
				Vertex v2 = mRotatedVertices.get(j);
				if (v.mPosX > v2.mPosX) {
					break;
				}
				if (v.mPosX == v2.mPosX && v.mPosY > v2.mPosY) {
					break;
				}
			}
			mRotatedVertices.add(j, v);
		}

		// Rotated rectangle lines/vertex indices. We need to find bounding
		// lines for rotated rectangle. After sorting vertices according to
		// their x -coordinate we don't have to worry about vertices at indices
		// 0 and 1. But due to inaccuracy it's possible vertex 3 is not the
		// opposing corner from vertex 0. So we are calculating distance from
		// vertex 0 to vertices 2 and 3 - and altering line indices if needed.
		// Also vertices/lines are given in an order first one has x -coordinate
		// at least the latter one. This property is used in getIntersections to
		// see if there is an intersection.
		/*사각형 라인 / 버텍스 인덱스를 회전. 우리는 경계 찾아야
회전된 사각형을위한 라인. 에 따라 정점을 정렬 후
  그들의 우리가 지표에서 정점에 대해 걱정할 필요가 없습니다 엑스 좌표
  0과 1. 하지만, 인해 부정확하기는 정점 3 않을 수도 있어요
  정점 0에서 모서리를 반대. 그래서 우리의 거리를 계산 아르
  버텍스의 정점 2, 3-0 - 그리고 필요한 경우 온라인 인덱스를 변경.
또한 꼭지점 / 라인 X 좌표를 가지고 주문 처음으로 부여됩니다
  후자의 한 적어도. 이 속성은하는 getIntersections에 사용됩니다
 교차로가있는 경우를 참조하십시오.*/
		int lines[][] = { { 0, 1 }, { 0, 2 }, { 1, 3 }, { 2, 3 } };
		{
			// TODO: There really has to be more 'easier' way of doing this -
			// not including extensive use of sqrt.
			/*정말로 sqrt의 광범위한 사용을 포함하여 이런 일을 더 '쉽게'방법이있을했습니다.*/
			Vertex v0 = mRotatedVertices.get(0);
			/*밑에 주석처럼 1,2로 변경만 했는데 아주 삼각형4개편대로 요상하게 넘겨지는걸 볼 수 있었음.*/
//			Vertex v2 = mRotatedVertices.get(1);
//			Vertex v3 = mRotatedVertices.get(2);
			Vertex v2 = mRotatedVertices.get(2);
			Vertex v3 = mRotatedVertices.get(3);
			/*x,y를 뒤집어 낫는데 별다른 변화가없었음*/
//			double dist2 = Math.sqrt((v0.mPosY - v2.mPosY)
//					* (v0.mPosY - v2.mPosY) + (v0.mPosX - v2.mPosX)
//					* (v0.mPosX - v2.mPosX));
//			double dist3 = Math.sqrt((v0.mPosY - v3.mPosY)
//					* (v0.mPosY - v3.mPosY) + (v0.mPosX - v3.mPosX)
//					* (v0.mPosX - v3.mPosX));
			double dist2 = Math.sqrt((v0.mPosX - v2.mPosX)
					* (v0.mPosX - v2.mPosX) + (v0.mPosY - v2.mPosY)
					* (v0.mPosY - v2.mPosY));
			double dist3 = Math.sqrt((v0.mPosX - v3.mPosX)
					* (v0.mPosX - v3.mPosX) + (v0.mPosY - v3.mPosY)
					* (v0.mPosY - v3.mPosY));
			if (dist2 > dist3) {
				/*3,2대입하는걸 2,3으로 해도 별변화는 없음*/
				lines[1][1] = 3;
				lines[2][1] = 2;
			}
		}

		mVerticesCountFront = mVerticesCountBack = 0;

		if (DRAW_SHADOW) {
			mTempShadowVertices.addAll(mDropShadowVertices);
			mTempShadowVertices.addAll(mSelfShadowVertices);
			mDropShadowVertices.clear();
			mSelfShadowVertices.clear();
		}

		// Length of 'curl' curve.
		double curlLength = Math.PI * radius;
		// Calculate scan lines.
		// TODO: Revisit this code one day. There is room for optimization here.
		mScanLines.clear();
		if (mMaxCurlSplits > 0) {
			mScanLines.add((double) 0);
		}
		for (int i = 1; i < mMaxCurlSplits; ++i) {
			mScanLines.add((-curlLength * i) / (mMaxCurlSplits - 1));
		}
		// As mRotatedVertices is ordered regarding x -coordinate, adding
		// this scan line produces scan area picking up vertices which are
		// rotated completely. One could say 'until infinity'.
		mScanLines.add(mRotatedVertices.get(3).mPosX - 1);

		// Start from right most vertex. Pretty much the same as first scan area
		// is starting from 'infinity'.
		double scanXmax = mRotatedVertices.get(0).mPosX + 1;

		for (int i = 0; i < mScanLines.size(); ++i) {
			// Once we have scanXmin and scanXmax we have a scan area to start
			// working with.
			double scanXmin = mScanLines.get(i);
			// First iterate 'original' rectangle vertices within scan area.
			for (int j = 0; j < mRotatedVertices.size(); ++j) {
				Vertex v = mRotatedVertices.get(j);
				// Test if vertex lies within this scan area.
				// TODO: Frankly speaking, can't remember why equality check was
				// added to both ends. Guessing it was somehow related to case
				// where radius=0f, which, given current implementation, could
				// be handled much more effectively anyway.
				if (v.mPosX >= scanXmin && v.mPosX <= scanXmax) {
					// Pop out a vertex from temp vertices.
					Vertex n = mTempVertices.remove(0);
					n.set(v);
					// This is done solely for triangulation reasons. Given a
					// rotated rectangle it has max 2 vertices having
					// intersection.
					Array<Vertex> intersections = getIntersections(
							mRotatedVertices, lines, n.mPosX);
					// In a sense one could say we're adding vertices always in
					// two, positioned at the ends of intersecting line. And for
					// triangulation to work properly they are added based on y
					// -coordinate. And this if-else is doing it for us.
					if (intersections.size() == 1
							&& intersections.get(0).mPosY > v.mPosY) {
						// In case intersecting vertex is higher add it first.
						mOutputVertices.addAll(intersections);
						mOutputVertices.add(n);
					} else if (intersections.size() <= 1) {
						// Otherwise add original vertex first.
						mOutputVertices.add(n);
						mOutputVertices.addAll(intersections);
					} else {
						// There should never be more than 1 intersecting
						// vertex. But if it happens as a fallback simply skip
						// everything.
						mTempVertices.add(n);
						mTempVertices.addAll(intersections);
					}
				}
			}

			// Search for scan line intersections.
			/*intersections=교차로*/
			Array<Vertex> intersections = getIntersections(mRotatedVertices,
					lines, scanXmin);

			// We expect to get 0 or 2 vertices. In rare cases there's only one
			// but in general given a scan line intersecting rectangle there
			// should be 2 intersecting vertices.
			if (intersections.size() == 2) {
				// There were two intersections, add them based on y
				// -coordinate, higher first, lower last.
				Vertex v1 = intersections.get(0);
				Vertex v2 = intersections.get(1);
				if (v1.mPosY < v2.mPosY) {
					mOutputVertices.add(v2);
					mOutputVertices.add(v1);
				} else {
					mOutputVertices.addAll(intersections);
				}
			} else if (intersections.size() != 0) {
				// This happens in a case in which there is a original vertex
				// exactly at scan line or something went very much wrong if
				// there are 3+ vertices. What ever the reason just return the
				// vertices to temp vertices for later use. In former case it
				// was handled already earlier once iterating through
				// mRotatedVertices, in latter case it's better to avoid doing
				// anything with them.
				mTempVertices.addAll(intersections);
			}

			// Add vertices found during this iteration to vertex etc buffers.
			while (mOutputVertices.size() > 0) {
				Vertex v = mOutputVertices.remove(0);
				mTempVertices.add(v);

				// Untouched vertices.
				if (i == 0) {
					//v.mAlpha = 0f;
					//修正縛を白に
					v.mAlpha = mFlipTexture ? BACKFACE_ALPHA : FRONTFACE_ALPHA;
					//前の半分
					mVerticesCountFront++;
				}
				// 'Completely' rotated vertices.
				else if (i == mScanLines.size() - 1 || curlLength == 0) {
					v.mPosX = -(curlLength + v.mPosX);
					v.mPosZ = 2 * radius;
					v.mPenumbraX = -v.mPenumbraX;
					//修正縛を白に
					v.mAlpha = 0f;
					//v.mAlpha = mFlipTexture ? FRONTFACE_ALPHA : BACKFACE_ALPHA;
					mVerticesCountBack++;
				}
				// Vertex lies within 'curl'.
				else {
					// Even though it's not obvious from the if-else clause,
					// here v.mPosX is between [-curlLength, 0]. And we can do
					// calculations around a half cylinder.
					double rotY = Math.PI * (v.mPosX / curlLength);
					v.mPosX = radius * Math.sin(rotY);
					v.mPosZ = radius - (radius * Math.cos(rotY));
					v.mPenumbraX *= Math.cos(rotY);
					// Map color multiplier to [.1f, 1f] range.
					v.mColor = .1f + .9f * Math.sqrt(Math.sin(rotY) + 1);

					if (v.mPosZ >= radius) {
						v.mAlpha = 0f;
						//修正縛を白に
						//v.mAlpha = mFlipTexture ? FRONTFACE_ALPHA : BACKFACE_ALPHA;
						//後ろの文字
						mVerticesCountBack++;
					} else {
						//v.mAlpha=0f;
						//修正縛を白に
						v.mAlpha = mFlipTexture ? BACKFACE_ALPHA : FRONTFACE_ALPHA;
						//前の文字の半分
						mVerticesCountFront++;
					}
				}

				// Move vertex back to 'world' coordinates.
				v.rotateZ(curlAngle);
				v.translate(curlPos.x, curlPos.y);
				addVertex(v);

				// Drop shadow is cast 'behind' the curl.
				if (DRAW_SHADOW && v.mPosZ > 0 && v.mPosZ <= radius) {
					ShadowVertex sv = mTempShadowVertices.remove(0);
					sv.mPosX = v.mPosX;
					sv.mPosY = v.mPosY;
					sv.mPosZ = v.mPosZ;
					sv.mPenumbraX = (v.mPosZ / 2) * -curlDir.x;
					sv.mPenumbraY = (v.mPosZ / 2) * -curlDir.y;
					sv.mPenumbraColor = v.mPosZ / radius;
					int idx = (mDropShadowVertices.size() + 1) / 2;
					mDropShadowVertices.add(idx, sv);
				}
				// Self shadow is cast partly over mesh.
				if (DRAW_SHADOW && v.mPosZ > radius) {
					ShadowVertex sv = mTempShadowVertices.remove(0);
					sv.mPosX = v.mPosX;
					sv.mPosY = v.mPosY;
					sv.mPosZ = v.mPosZ;
					sv.mPenumbraX = ((v.mPosZ - radius) / 3) * v.mPenumbraX;
					sv.mPenumbraY = ((v.mPosZ - radius) / 3) * v.mPenumbraY;
					sv.mPenumbraColor = (v.mPosZ - radius) / (2 * radius);
					int idx = (mSelfShadowVertices.size() + 1) / 2;
					mSelfShadowVertices.add(idx, sv);
				}
			}

			// Switch scanXmin as scanXmax for next iteration.
			scanXmax = scanXmin;
		}

		mVertices.position(0);
		mColors.position(0);
		if (DRAW_TEXTURE) {
			mTexCoords.position(0);
		}

		// Add shadow Vertices.
		if (DRAW_SHADOW) {
			mShadowColors.position(0);
			mShadowVertices.position(0);
			mDropShadowCount = 0;

			for (int i = 0; i < mDropShadowVertices.size(); ++i) {
				ShadowVertex sv = mDropShadowVertices.get(i);
				mShadowVertices.put((float) sv.mPosX);
				mShadowVertices.put((float) sv.mPosY);
				mShadowVertices.put((float) sv.mPosZ);
				mShadowVertices.put((float) (sv.mPosX + sv.mPenumbraX));
				mShadowVertices.put((float) (sv.mPosY + sv.mPenumbraY));
				mShadowVertices.put((float) sv.mPosZ);
				for (int j = 0; j < 4; ++j) {
					double color = SHADOW_OUTER_COLOR[j]
							+ (SHADOW_INNER_COLOR[j] - SHADOW_OUTER_COLOR[j])
							* sv.mPenumbraColor;
					mShadowColors.put((float) color);
				}
				mShadowColors.put(SHADOW_OUTER_COLOR);
				mDropShadowCount += 2;
			}
			mSelfShadowCount = 0;
			for (int i = 0; i < mSelfShadowVertices.size(); ++i) {
				ShadowVertex sv = mSelfShadowVertices.get(i);
				/*mShadowVertices 전부 주석처리함*/
				mShadowVertices.put((float) sv.mPosX);
				mShadowVertices.put((float) sv.mPosY);
				mShadowVertices.put((float) sv.mPosZ);
				mShadowVertices.put((float) (sv.mPosX + sv.mPenumbraX));
				mShadowVertices.put((float) (sv.mPosY + sv.mPenumbraY));
				mShadowVertices.put((float) sv.mPosZ);
				for (int j = 0; j < 4; ++j) {
					double color = SHADOW_OUTER_COLOR[j]
							+ (SHADOW_INNER_COLOR[j] - SHADOW_OUTER_COLOR[j])
							* sv.mPenumbraColor;
					mShadowColors.put((float) color);
				}
				mShadowColors.put(SHADOW_OUTER_COLOR);
				mSelfShadowCount += 2;
			}
			mShadowColors.position(0);
			mShadowVertices.position(0);
		}
	}/*컬 메소드끝*/
/*ㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁ*/
	/**
	 * Draws our mesh.
	 */
	public synchronized void draw(GL10 gl) {
		// First allocate texture if there is not one yet.
		if (DRAW_TEXTURE && mTextureIds == null) {
			// Generate texture.
			mTextureIds = new int[1];
			/*밑에gl은 화면단인듯 넘기는 듯한 이벤트와는 다름*/
			gl.glGenTextures(1, mTextureIds, 0);
			// Set texture attributes.
			gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextureIds[0]);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
					GL10.GL_LINEAR);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
					GL10.GL_LINEAR);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,
					GL10.GL_CLAMP_TO_EDGE);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,
					GL10.GL_CLAMP_TO_EDGE);
		}
		// If mBitmap != null we have a new texture.
		if (DRAW_TEXTURE && mBitmap != null) {
			gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextureIds[0]);/*역시나 없으면 딱딱함*/
			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, mBitmap, 0);/*또한번 흥미로운 결과가!!이거없으면 페인트에 붙인 그림들 하나도 안보이고 흰종이만 남음*/
			mBitmap.recycle();
			mBitmap = null;
		}

		if (DRAW_TEXTURE) {
			/*이거없으니까 뭔가 그림이 딱딱하게 변하는게 눈에 보임TEXTURE 말그대로 조화,질감,촉감이 이상해짐*/
			gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextureIds[0]);
		}

		// Some 'global' settings.
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);/*이것도 딱히 이벤트와는 관계가없음*/

		// TODO: Drop shadow drawing is done temporarily here to hide some
		// problems with its calculation.
		if (DRAW_SHADOW) {
			/*gl이열도 다주석해봤으니 이벤트와는 직접적인관계가,,*/
			gl.glEnable(GL10.GL_BLEND);
			gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
			gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
			gl.glColorPointer(4, GL10.GL_FLOAT, 0, mShadowColors);
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mShadowVertices);
			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, mDropShadowCount);
			gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
			gl.glDisable(GL10.GL_BLEND);
		}

		// Enable texture coordinates.
		if (DRAW_TEXTURE) {
			/*이쪽gl도 마찬가지로 화면이안보이기만함*/
			gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
			gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTexCoords);
		}
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertices);

		// Enable color array.
		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
		gl.glColorPointer(4, GL10.GL_FLOAT, 0, mColors);

		// Draw blank / 'white' front facing vertices.
		gl.glDisable(GL10.GL_TEXTURE_2D);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, mVerticesCountFront);
		// Draw front facing texture.
		// TODO: Decide whether it's really needed to have alpha blending for
		// front facing texture. If not, GL_BLEND isn't needed, possibly
		// increasing performance. The heck, is it needed at all?
		if (DRAW_TEXTURE) {
			gl.glEnable(GL10.GL_BLEND);
			gl.glEnable(GL10.GL_TEXTURE_2D);
			gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, mVerticesCountFront);
			gl.glDisable(GL10.GL_TEXTURE_2D);
			gl.glDisable(GL10.GL_BLEND);
		}
		int backStartIdx = Math.max(0, mVerticesCountFront - 2);
		int backCount = mVerticesCountFront + mVerticesCountBack - backStartIdx;
		// Draw blank / 'white' back facing vertices.
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, backStartIdx, backCount);
		// Draw back facing texture.
		if (DRAW_TEXTURE) {
			gl.glEnable(GL10.GL_BLEND);
			gl.glEnable(GL10.GL_TEXTURE_2D);
			gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, backStartIdx, backCount);
			gl.glDisable(GL10.GL_TEXTURE_2D);
			gl.glDisable(GL10.GL_BLEND);
		}

		// Disable textures and color array.
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glDisableClientState(GL10.GL_COLOR_ARRAY);

		if (DRAW_POLYGON_OUTLINES) {
			gl.glEnable(GL10.GL_BLEND);
			gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
			gl.glLineWidth(1.0f);
			gl.glColor4f(0.5f, 0.5f, 1.0f, 1.0f);
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertices);
			gl.glDrawArrays(GL10.GL_LINE_STRIP, 0, mVerticesCountFront);
			gl.glDisable(GL10.GL_BLEND);
		}

		if (DRAW_CURL_POSITION) {
			/*여기gl을 다 주석해도 변한것은 없었다*/
			gl.glEnable(GL10.GL_BLEND);
			gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
			gl.glLineWidth(1.0f);
			gl.glColor4f(1.0f, 0.5f, 0.5f, 1.0f);
			gl.glVertexPointer(2, GL10.GL_FLOAT, 0, mCurlPositionLines);
			gl.glDrawArrays(GL10.GL_LINES, 0, mCurlPositionLinesCount * 2);
			gl.glDisable(GL10.GL_BLEND);
		}

		if (DRAW_SHADOW) {
			/*여기gl을 다 주석해도 변한것은 없었다*/
			gl.glEnable(GL10.GL_BLEND);
			gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
			gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
			gl.glColorPointer(4, GL10.GL_FLOAT, 0, mShadowColors);
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mShadowVertices);
			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, mDropShadowCount,
					mSelfShadowCount);
			gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
			gl.glDisable(GL10.GL_BLEND);
			
		}

		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);/*여기도 변한게없소*/
	}

	/**
	 * Resets mesh to 'initial' state. Meaning this mesh will draw a plain
	 * textured rectangle after call to this method.
	 */
	public synchronized void reset() {
		mVertices.position(0);
		mColors.position(0);
		if (DRAW_TEXTURE) {
			mTexCoords.position(0);
		}
		
		for (int i = 0; i < 4; ++i) {
			addVertex(mRectangle[i]);
		}
		mVerticesCountFront = 4;
		mVerticesCountBack = 0;
		mVertices.position(0);
		mColors.position(0);
		if (DRAW_TEXTURE) {
			mTexCoords.position(0);
		}

		mDropShadowCount = mSelfShadowCount = 0;
	}

	/**
	 * Resets allocated texture id forcing creation of new one. After calling
	 * this method you most likely want to set bitmap too as it's lost. This
	 * method should be called only once e.g GL context is re-created as this
	 * method does not release previous texture id, only makes sure new one is
	 * requested on next render.
	 */
	public synchronized void resetTexture() {
		mTextureIds = null;
	}

	/**
	 * Sets new texture for this mesh.
	 */
	public synchronized void setBitmap(Bitmap bitmap) {
		mBitmap = null;
		if (DRAW_TEXTURE) {
			// Bitmap original size.
/*이렇게 두개 셋팅을 바꾸면 실제이미지만 작아져서 넘겨짐*/
			
			int w = bitmap.getWidth();
			int h = bitmap.getHeight();
//			int w = bitmap.getHeight();
//			int h = bitmap.getWidth();
			// Bitmap size expanded to next power of two. This is done due to
			// the requirement on many devices, texture width and height should
			// be power of two.
			/*비트맵 크기가 두 옆에 전원 확장. 이것은 여러 장치, 텍스처 너비와
			 *  높이가 두 힘이 있어야합니다에 대한 요구로 인해 이루어집니다.*/
			
			int newW = getNextHighestPO2(w);
			int newH = getNextHighestPO2(h);
			// TODO: Is there another way to create a bigger Bitmap and copy
			// original Bitmap to it more efficiently? Immutable bitmap anyone?
			/*TODO가 :보다 효율적으로 그것에 큰 비트맵 복사 비트맵 원본을 만들 수있다
			 *  다른 방법 있나요? 불변의 비트맵 누구?*/
			mBitmap = Bitmap.createBitmap(newW, newH, bitmap.getConfig());//previewエーらの原因
			Canvas c = new Canvas(mBitmap);
			c.drawBitmap(bitmap, 0, 0, null);
			
			// Calculate final texture coordinates.
			float texX = (float) w / newW;
			float texY = (float) h / newH;
			mTextureRect.set(0f, 0f, texX, texY);
			if (mFlipTexture) {
				setTexCoords(texX, 0f, 0f, texY);
			} else {
				setTexCoords(0f, 0f, texX, texY);
			}
		}
		bitmap.recycle();
		bitmap = null;
	}

	/**
	 * If true, flips texture sideways.
	 */
	/*true이면, 텍스처 옆으로 팅겨주는군요.*/
	public synchronized void setFlipTexture(boolean flipTexture) {
		/*mFlipTexture = flipTexture;

		if (mFlipTexture) {
			setTexCoords(mTextureRect.left, mTextureRect.top,
					mTextureRect.right, mTextureRect.bottom);
			
		}/* else {
			setTexCoords(mTextureRect.left, mTextureRect.top,
					mTextureRect.right, mTextureRect.bottom);
		}*/
		
		//修正AｌPhaを0に
		/*for (int i = 0; i < 4; ++i) {
			mRectangle[i].mAlpha = mFlipTexture ? BACKFACE_ALPHA
					: FRONTFACE_ALPHA;
		}*/
		
	}

	/**
	 * Update mesh bounds.
	 */
	public void setRect(RectF r) {/*왜이렇게 굳이 앵글이 많을까?*/
		/*0=왼쪽상단 1=아래-왼쪽 2=오른쪽-상단 3=오른쪽 하단*/
		
		mRectangle[0].mPosX = r.left;
		mRectangle[0].mPosY = r.top;
		mRectangle[1].mPosX = r.left;
		mRectangle[1].mPosY = r.bottom;
		mRectangle[2].mPosX = r.right;
		mRectangle[2].mPosY = r.top;
		mRectangle[3].mPosX = r.right;
		mRectangle[3].mPosY = r.bottom;
		
		/*흥미로운 현상이긴한데 이벤트와는 관계없을 듯 조금더 넒적하게 나오고 다음페이지를 넘기면
		 * 밑에다음장이 생겨버림 확실히 이벤트와 관련없다는걸 증명하게됨*/
//		mRectangle[0].mPosX = r.bottom;
//		mRectangle[0].mPosY = r.right;
//		mRectangle[1].mPosX = r.bottom;
//		mRectangle[1].mPosY = r.left;
//		mRectangle[2].mPosX = r.top;
//		mRectangle[2].mPosY = r.right;
//		mRectangle[3].mPosX = r.top;
//		mRectangle[3].mPosY = r.left;
		 /* 꽤 이벤트와 근접하다 생각했는데 아쉽게도 그림 이미지자체가 뒤집어져버렸음
		 * 주석문 처럼 숫자를 조금 바꿔서,,,,*/
//		mRectangle[1].mPosX = r.left;
//		mRectangle[1].mPosY = r.top;
//		mRectangle[0].mPosX = r.left;
//		mRectangle[0].mPosY = r.bottom;
//		mRectangle[3].mPosX = r.right;
//		mRectangle[3].mPosY = r.top;
//		mRectangle[2].mPosX = r.right;
//		mRectangle[2].mPosY = r.bottom;
	}

	/**
	 * Adds vertex to buffers.
	 */
	/*꽤나 결정적으로 이벤트의 변화가 보여짐*/
	private void addVertex(Vertex vertex) {
		mVertices.put((float) vertex.mPosX);
		mVertices.put((float) vertex.mPosY);
		mVertices.put((float) vertex.mPosZ);/*여러 개로 쪼개져 지면서 재밌는 모습이 나타남 */
		mColors.put((float) vertex.mColor);
		mColors.put((float) vertex.mColor);
		mColors.put((float) vertex.mColor);
		mColors.put((float) vertex.mAlpha);/*넘어갈때 뒷장에 종이에 알파값이 없어지는게 티가남*/
		if (DRAW_TEXTURE) {
			mTexCoords.put((float) vertex.mTexX);
			mTexCoords.put((float) vertex.mTexY);
		}
	}

	/**
	 * Calculates intersections for given scan line.
	 */
	/*주어진 스캔 라인에 대한 교차로를 계산합니다.*/
	private Array<Vertex> getIntersections(Array<Vertex> vertices,
			int[][] lineIndices, double scanX) {
		mIntersections.clear();
		// Iterate through rectangle lines each re-presented as a pair of
		// vertices.
		for (int j = 0; j < lineIndices.length; j++) {
			/*숫자 배열에 0,1을 1,0으로 변경하니까 딱딱하게 넘어감,,ㅋ신GI*/
//			Vertex v1 = vertices.get(lineIndices[j][1]);
//			Vertex v2 = vertices.get(lineIndices[j][0]);
			Vertex v1 = vertices.get(lineIndices[j][0]);
			Vertex v2 = vertices.get(lineIndices[j][1]);
			// Here we expect that v1.mPosX >= v2.mPosX and wont do intersection
			// test the opposite way.
			if (v1.mPosX > scanX && v2.mPosX < scanX) {
				// There is an intersection, calculate coefficient telling 'how
				// far' scanX is from v2.
				double c = (scanX - v2.mPosX) / (v1.mPosX - v2.mPosX);
				Vertex n = mTempVertices.remove(0);
				n.set(v2);
				n.mPosX = scanX;
				n.mPosY += (v1.mPosY - v2.mPosY) * c;
				if (DRAW_TEXTURE) {
					n.mTexX += (v1.mTexX - v2.mTexX) * c;
					n.mTexY += (v1.mTexY - v2.mTexY) * c;
				}
				if (DRAW_SHADOW) {
					n.mPenumbraX += (v1.mPenumbraX - v2.mPenumbraX) * c;
					n.mPenumbraY += (v1.mPenumbraY - v2.mPenumbraY) * c;
				}
				mIntersections.add(n);
			}
		}
		return mIntersections;
	}

	/**
	 * Calculates the next highest power of two for a given integer.
	 */
	private int getNextHighestPO2(int n) {
		n -= 1;
		n = n | (n >> 1);
		n = n | (n >> 2);
		n = n | (n >> 4);
		n = n | (n >> 8);
		n = n | (n >> 16);
		n = n | (n >> 32);
		return n + 1;
	}

	/**
	 * Sets texture coordinates to mRectangle vertices.
	 */
	private synchronized void setTexCoords(float left, float top, float right,
			float bottom) {
		/*mRectangle[0]이게 없으면 안에만 그림이 안보임*/
		mRectangle[0].mTexX = left;
		mRectangle[0].mTexY = top;
		mRectangle[1].mTexX = left;
		mRectangle[1].mTexY = bottom;
		mRectangle[2].mTexX = right;
		mRectangle[2].mTexY = top;
		mRectangle[3].mTexX = right;
		mRectangle[3].mTexY = bottom;
	}

	/**
	 * Simple fixed size array implementation.
	 */
	/*간단한 고정 크기 배열 구현.*/
	private class Array<T> {
		private Object[] mArray;
		private int mSize;
		private int mCapacity;

		public Array(int capacity) {
			mCapacity = capacity;
			mArray = new Object[capacity];
		}

		public void add(int index, T item) {
			if (index < 0 || index > mSize || mSize >= mCapacity) {
				throw new IndexOutOfBoundsException();
			}
			for (int i = mSize; i > index; --i) {
				mArray[i] = mArray[i - 1];
			}
			mArray[index] = item;
			++mSize;
		}

		public void add(T item) {
			if (mSize >= mCapacity) {
				throw new IndexOutOfBoundsException();
			}
			mArray[mSize++] = item;
		}

		public void addAll(Array<T> array) {
			if (mSize + array.size() > mCapacity) {
				throw new IndexOutOfBoundsException();
			}
			for (int i = 0; i < array.size(); ++i) {
				mArray[mSize++] = array.get(i);
			}
		}

		public void clear() {
			mSize = 0;
		}

		@SuppressWarnings("unchecked")
		public T get(int index) {
			if (index < 0 || index >= mSize) {
				throw new IndexOutOfBoundsException();
			}
			return (T) mArray[index];
		}

		@SuppressWarnings("unchecked")
		/*리무브 메소드 전체를지우니까 아에 터치액션누르면 에러남*/
		public T remove(int index) {
			if (index < 0 || index >= mSize) {
				throw new IndexOutOfBoundsException();
			}
			T item = (T) mArray[index];
			for (int i = index; i < mSize - 1; ++i) {
				mArray[i] = mArray[i + 1];
			}
			--mSize;
			return item;
		}

		public int size() {
			return mSize;
		}

	}

	/**
	 * Holder for shadow vertex information.
	 */
	private class ShadowVertex {
		public double mPosX;
		public double mPosY;
		public double mPosZ;
		public double mPenumbraX;
		public double mPenumbraY;
		public double mPenumbraColor;
	}

	/**
	 * Holder for vertex information.
	 */
	/*그냥 셋팅할려는거아닐까*/
	private class Vertex {
		public double mPosX;
		public double mPosY;
		public double mPosZ;
		public double mTexX;
		public double mTexY;
		public double mPenumbraX;
		public double mPenumbraY;
		public double mColor;
		public double mAlpha;

		/*0으로 일단 다초기화 시키는 것도 그렇고*/
		public Vertex() {
			mPosX = mPosY = mPosZ = mTexX = mTexY = 0;
			mColor = mAlpha = 1;
		}

		public void rotateZ(double theta) {
			double cos = Math.cos(theta);
			double sin = Math.sin(theta);
		/*double x,y를 만지면서 느낀건 좌표값만 원하는대로 바꾸면 컨트롤 가능한게 아닐까라는 추론까지감*/
			/*좌표를 거꾸로 바꾸니까 양탄자 날라가듯이 넘겨짐,,ㅋㅋ*/
			double x = mPosX * cos + mPosY * sin;
			double y = mPosX * -sin + mPosY * cos;
	
			mPosX = x;
			mPosY = y;
			double px = mPenumbraX * cos + mPenumbraY * sin;
			double py = mPenumbraX * -sin + mPenumbraY * cos;
          /*반그늘, 반그림자*/
			mPenumbraX = px;
			mPenumbraY = py;
		}

		public void set(Vertex vertex) {
			mPosX = vertex.mPosX;
			mPosY = vertex.mPosY;
			mPosZ = vertex.mPosZ;
			mTexX = vertex.mTexX;
			mTexY = vertex.mTexY;
			mPenumbraX = vertex.mPenumbraX;
			mPenumbraY = vertex.mPenumbraY;
			mColor = vertex.mColor;
			mAlpha = vertex.mAlpha;
		}

		public void translate(double dx, double dy) {
			/*아마이건 터치때마다 값을 받아오는것 같네요,,*/
			mPosX += dx;
			mPosY += dy;
		}
	}
}
