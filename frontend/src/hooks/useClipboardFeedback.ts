export function useClipboardFeedback() {
  return { feedback: '', reset: () => {}, copy: async (_text: string) => {} };
}
