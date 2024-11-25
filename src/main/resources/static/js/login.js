document.addEventListener("DOMContentLoaded", () => {
    const signUpBtn = document.getElementById("signUp");
    const signInBtn = document.getElementById("signIn");
    const container = document.querySelector(".container");

    // 버튼 이벤트: 폼 전환
    signUpBtn.addEventListener("click", () => {
        container.classList.add("right-panel-active");
    });

    signInBtn.addEventListener("click", () => {
        container.classList.remove("right-panel-active");
    });

    // 회원가입 폼 전송
    const signupForm = document.getElementById("signupForm");
    signupForm.addEventListener("submit", async (e) => {
        e.preventDefault(); // 기본 폼 제출 동작 방지
        const formData = new FormData(signupForm);

        // 폼 데이터 확인
        console.log("Sign Up Form Data:");
        for (let [key, value] of formData.entries()) {
            console.log(`${key}: ${value}`);
        }

        try {
            const response = await fetch("/signup", {
                method: "POST",
                body: formData
            });
            if (response.ok) {
                alert("Sign up successful!");
                location.href = "/login";
            } else {
                const error = await response.text();
                alert("Sign up failed: " + error);
            }
        } catch (error) {
            console.error("Sign up error:", error);
        }
    });

    // 로그인 폼 전송
    const signinForm = document.getElementById("signinForm");
    signinForm.addEventListener("submit", async (e) => {
        e.preventDefault(); // 기본 폼 제출 동작 방지
        const formData = new FormData(signinForm);

        // 폼 데이터 확인
        console.log("Sign In Form Data:");
        for (let [key, value] of formData.entries()) {
            console.log(`${key}: ${value}`);
        }

        try {
            const response = await fetch("/login", {
                method: "POST",
                body: formData
            });
            if (response.ok) {
                alert("Sign in successful!");
                location.href = "/main";
            } else {
                const error = await response.text();
                alert("Sign in failed: " + error);
            }
        } catch (error) {
            console.error("Sign in error:", error);
        }
    });
});
