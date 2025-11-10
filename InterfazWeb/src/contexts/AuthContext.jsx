import { createContext, useContext } from 'react';
import { useGetAuth } from '../hooks/useGetAuth';

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const auth = useGetAuth();
  
  return (
    <AuthContext.Provider value={auth}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  return useContext(AuthContext);
};
