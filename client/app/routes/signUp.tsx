import { SignUp } from "@clerk/clerk-react";

export default function Register() {

  return (
    <div className="flex justify-center">
      <div className='py-8 text-center'>
        <h1 className='text-4xl text-black font-bold mb-4'>Sign Up</h1>
        <SignUp />
      </div>
    </div> 
  );
};