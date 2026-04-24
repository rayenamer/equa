export interface SignupRequestDTO {
  username: string;
  email: string;
  password: string;
  userType: string; // 'ASSISTANT' or 'OBSERVER'
  permissions?: string[]; // optional, for OBSERVER
  recaptchaToken?: string; // optional, if reCAPTCHA is enabled
}

export interface SigninRequestDTO {
  email: string;
  password: string;
  recaptchaToken?: string; // optional
}

export interface AuthResponseDTO {
  token: string;
  type: string;
  id: number;
  username: string;
  email: string;
  userType: string;
}

export interface User {
  id: number;
  username: string;
  email: string;
  userType: string;
}