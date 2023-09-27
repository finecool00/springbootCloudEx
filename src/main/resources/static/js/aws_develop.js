const dropbox = document.querySelector('.file_box');
const input_filename = document.querySelector('.file_name');

const file_btn = document.querySelector('.upload_btn');

const list_bucket_objects = document.querySelector('.list_bucket_objects');

const bucket_obj_name = document.querySelector('input[name="bucket_obj_name"]');
const delete_bucket_objects = document.querySelector(".delete_bucket_objects");

let file_data;

//박스 안에 drag 하고 있을 때
dropbox.addEventListener('dragover', function (e) {
  e.preventDefault();
  this.style.backgroundColor = 'rgb(13 110 253 / 25%)';
});

//박스 밖으로 drag가 나갈 때
dropbox.addEventListener('dragleave', function (e) {
  this.style.backgroundColor = 'white';
});

//박스 안에 drop 했을 때
dropbox.addEventListener('drop', function (e) {
  e.preventDefault();
  //데이터 크기 검사  
  let byteSize= e.dataTransfer.files[0].size;
  let maxSize = 50;
    
  if( byteSize / 1000000 > maxSize) {
	  alert("파일은 최대 50MB이하만 허용됩니다");
	  return;
  } else {
	  //백그라운드 색상변경
  	  this.style.backgroundColor = 'white';
	  //파일 이름을 text로 표시
	  let filename = e.dataTransfer.files[0].name;
	  input_filename.innerHTML = filename;
		
	  //파일 데이터를 변수에 저장
	  file_data = e.dataTransfer.files[0];
  }
    
});

//객체업로드
file_btn.addEventListener('click', function(e) {
	
	let formData = new FormData();
	formData.append('file_data' , file_data);
	
	fetch('/cloudUpload', {method: 'post', body: formData})
	.then(response => response.text() )
	.then(data => {
		alert(data);
	})
	.catch(err => alert('업로드에 실패했습니다:' + err) );
	
})

//버킷의 객체 목록 확인
list_bucket_objects.addEventListener('click', function(e) {
	fetch('/list_bucket_objects')
	.then(response => response.text())
	.then(data => {
		alert(data);
	})
	.catch(err => alert('목록 조회에 실패했습니다' + err));
})


//버킷의 객체 삭제
delete_bucket_objects.addEventListener('click', function(e) {
	
	let formData = new FormData();
	formData.append('bucket_obj_name', bucket_obj_name.value);
	
	fetch('/delete_bucket_objects', {method:'delete', body:formData})
	.then(response => response.text() )
	.then(data => {
		alert(data);
	})
	.catch(err => alert('삭제에 실패했습니다' + err));
	
});



/////////////////////////////////////////////////////////////////////////
//람다호출

const lambda_call = document.querySelector(".lambda_call");

lambda_call.addEventListener('click', function(e) {
	fetch('/lambda_call')
	.then(response => response.text())
	.then(data => {
		alert(data);
	})
	.catch(err => alert('호출 실패하였습니다' + err));
})


////////////////////////////////////////////////////////////////////////

const send_email = document.querySelector(".send_email");

send_email.addEventListener('click', function(e) {
	
	fetch('/send_email')
	.then(response => response.text())
	.then(data => console.log(data))
	.catch(e => console.log('메일전송실패' + e));
});


const send_sns = document.querySelector(".send_sns");

send_sns.addEventListener('click', function(e) {
	
	fetch('/send_sns')
	.then(response => response.text())
	.then(data => console.log(data))
	.catch(e => console.log('메시지 게시 실패 : ' + e));
});


const poll_sqs = document.querySelector(".poll_sqs");

poll_sqs.addEventListener('click', function(e) {
	
	fetch('/poll_sqs')
	.then(response => response.text())
	.then(data => console.log(data))
	.catch(e => console.log('메시기 게시 당기기 실패 :' + e));
});