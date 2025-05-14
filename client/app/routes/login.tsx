import { SignIn } from '@clerk/clerk-react';

export default function Login() {

  return (
    <div className="flex justify-center" аria-label="Login">
      <div className='py-8 text-center'>
        <h1 className='text-4xl text-black font-bold mb-4' >Sign In</h1>
        <SignIn/>
      </div>
    </div> 
  );
};