// ─── yudao API 封装 ──────────────────────────────────────────────────
// 契约依据: docs/04-API-CONTRACT.md
// yudao 统一响应: { code, data, msg },code === 0 为成功

export async function post<T>(path: string, body: unknown): Promise<T> {
  let res: Response;
  try {
    res = await fetch(path, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(body),
    });
  } catch {
    throw new Error('网络错误,请稍后再试');
  }
  const result = await res.json().catch(() => null);
  if (result && result.code === 0) {
    return result.data as T;
  }
  throw new Error((result && result.msg) || '提交失败,请稍后再试');
}
