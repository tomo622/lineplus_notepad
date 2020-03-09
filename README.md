# LINE 신입 개발자 채용 앱 개발 챌린지
- 과제 내용
  <p align="center">
  	<img src="https://user-images.githubusercontent.com/19742979/76165978-8e660880-619e-11ea-8a30-d52dfb1c1d1d.png">
  </p>
- 요구사항 *(기억나는 대로만 정리함)*
  - Min SDK Version: 21
    Target SDK Version: 27
  - 메모 리스트가 보여집니다. 리스트에는 제목, 내용, 사진 섬네일이 출력됩니다. (사진이 여러 개인 경우 첫 번째 사진으로 섬네일을 출력합니다.)
  - 메모 리스트를 눌러 상세보기 화면으로 이동합니다. 상세보기 화면에서는 제목과 내용, 저장된 사진을 확인할 수 있고 메뉴 버튼을 통해 수정, 삭제할 수 있습니다.
  - 사진 첨부의 경우 메모 중간에 첨부되는 방식이 아니고 별도로 첨부하는 방식입니다.
  - 사진 첨부의 방법은 사진 촬영, 사진첩으로 부터 가져오기, 이미지 링크로 가져오기 세 가지가 있습니다. 단, 이미지를 가져올 수 없는 경우에 대해서 처리가 필요합니다.

---

- 개발 기간: 2020.02.16 ~ 2020.02.24
- 개발환경: Android Studio
- 개발언어: Java
- 데이터베이스: SQLite(내부 DB)
- 사용 라이브러리: Picasso (이미지 로드)
- 기타: Observer pattern, Singleton pattern

---

###### 데이터 관리

 테이블은 간단하게 제목과 날짜, 내용 컬럼을 갖는 메모 테이블과 이미지 경로(이미지 링크인 경우 URL) 컬럼을 갖는 이미지 테이블로 구성된다. 이 둘은 1:N(=메모:이미지) 관계를 갖는다.
 저장되는 이미지는 내부 파일 저장소를 이용했다. 이미지 테이블에는 이곳에 저장된 이미지의 경로가 저장되는 것이다. (이미지 링크인 경우 예외)

###### 데이터 출력

 메모의 추가, 수정, 삭제 이벤트 발생에 대한 화면 갱신은 옵저버 패턴을 이용하여 구현했다.
 Picasso 라이브러리를 통해 이미지 뷰에 이미지를 출력해줬다. 대상 이미지 뷰와 함께 출력하려는 이미지의 내부 저장소 경로나 URI를 파라미터로 넘겨주기만하면 되기 때문에 굉장히 편리했다. 또한 이미지 로드 실패에 대한 리스너 오버라이드 함수를 제공하고 있어 실패 처리에 대해서도 간단하게 구현할 수 있었다.

---
<p align="center">
	<img src="https://user-images.githubusercontent.com/19742979/76195751-5907fc00-622c-11ea-905d-3c27bdf99014.png"  width="60%">
</p>
<p align="center"><메모 목록 및 상세보기 화면></p>
<p align="center">
	<img src="https://user-images.githubusercontent.com/19742979/76195756-5a392900-622c-11ea-8fe8-8ce47fa996d8.png" align="center" width="100%">
</p>
<p align="center"><저장된 이미지 화면></p>
<p align="center">
	<img src="https://user-images.githubusercontent.com/19742979/76195757-5ad1bf80-622c-11ea-852c-964565682fa3.png" align="center" width="100%">
</p>
<p align="center"><이미지 추가 방법></p>

---

###### 개선사항 및 아쉬운 점

1. Observer pattern을 직접 구현하지 말고 RxAndroid 라이브러리를 사용해보면 좋았을 것 같다.
2. 사진 촬영을 위해 `ACTION_IMAGE_CAPTURE` Intent를 호출하여 얻는 결과물은 미리보기 이미지로써 이는 원본 크기의 이미지가 아니다. 원본 크기의 사진은 공용 외부 저장소에 저장되는데 이곳에 접근하여 파일을 읽고 쓰기 위한 권한이 필요하다.