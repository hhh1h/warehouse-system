use tauri::Manager;

#[cfg_attr(mobile, tauri::mobile_entry_point)]
pub fn run() {
    tauri::Builder::default()
        .plugin(tauri_plugin_shell::init())
        .setup(|app| {
            println!("仓库管理系统桌面版启动");

            if let Some(window) = app.get_webview_window("main") {
                let _ = window.set_title("仓库管理系统 v1.0.0");
                let _ = window.maximize();
            }

            Ok(())
        })
        .run(tauri::generate_context!())
        .expect("启动应用时发生错误");
}
