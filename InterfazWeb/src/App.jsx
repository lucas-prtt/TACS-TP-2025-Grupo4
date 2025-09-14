import { Routes, Route } from "react-router-dom"
import { DashBoard } from "./usuario/dashboard/DashBoard"
import { AppRouter } from "./router/AppRouter"
import { AppTheme } from "./assets/theme/AppTheme"

export const App = () => {
  return (
    <AppTheme>
      <AppRouter />
    </AppTheme>
  )
}
